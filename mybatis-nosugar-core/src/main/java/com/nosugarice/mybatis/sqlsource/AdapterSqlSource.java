/*
 *    Copyright 2021 NoSugarIce
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nosugarice.mybatis.sqlsource;

import com.nosugarice.mybatis.annotation.ProviderAdapter;
import com.nosugarice.mybatis.config.Constants;
import com.nosugarice.mybatis.config.DialectContext;
import com.nosugarice.mybatis.dialect.Dialect;
import com.nosugarice.mybatis.dialect.DialectFactory;
import com.nosugarice.mybatis.dialect.RuntimeDialect;
import com.nosugarice.mybatis.domain.Page;
import com.nosugarice.mybatis.exception.NoSugarException;
import com.nosugarice.mybatis.mapper.MapperParam;
import com.nosugarice.mybatis.mapper.select.SelectPageMapper;
import com.nosugarice.mybatis.sql.SQLConstants;
import com.nosugarice.mybatis.util.LambdaUtils;
import com.nosugarice.mybatis.util.LambdaUtils.LambdaInfo;
import com.nosugarice.mybatis.util.Preconditions;
import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.cache.decorators.LruCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.session.Configuration;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/9
 */
public class AdapterSqlSource implements SqlSource {

    private static final Field MAPPER_INTERFACE_FIELD;
    private static final LruCache METHOD_PARAM_NAME_CACHE;

    private final Configuration configuration;
    private final ProviderAdapter.Type adapterType;
    private final DialectFactory dialectFactory;

    public AdapterSqlSource(Configuration configuration, ProviderAdapter.Type adapterType, DialectFactory dialectFactory) {
        this.configuration = configuration;
        this.adapterType = adapterType;
        this.dialectFactory = dialectFactory;
    }

    static {
        try {
            MAPPER_INTERFACE_FIELD = MapperProxy.class.getDeclaredField("mapperInterface");
            MAPPER_INTERFACE_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new NoSugarException(e);
        }
        METHOD_PARAM_NAME_CACHE = new LruCache(new PerpetualCache("METHOD_PARAM_NAME_CACHE"));
        METHOD_PARAM_NAME_CACHE.setSize(2048);
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        BoundSql boundSql = null;
        Map<?, ?> parameterMap = ((Map<?, ?>) parameterObject);

        Object mapperFunction = parameterMap.get(MapperParam.MAPPER_FUNCTION);
        LambdaInfo lambdaInfo = LambdaUtils.getLambdaInfo((Serializable) mapperFunction);
        String functionalName = lambdaInfo.getMethodName();
        String className = lambdaInfo.getClassName();
        String statementId = className + "." + functionalName;
        if (!configuration.hasStatement(statementId)) {
            Class<?> targetMapperClass = getTargetMapperClass(lambdaInfo.getFirstCapturedArg());
            Preconditions.checkNotNull(targetMapperClass, functionalName + "未获取到真实Mapper类型.");
            className = targetMapperClass.getName();
            statementId = className + "." + functionalName;
        }
        Preconditions.checkArgument(configuration.hasStatement(statementId), functionalName + "没有构建Statement.");
        Preconditions.checkArgument(configuration.getMappedStatement(statementId, false)
                .getSqlCommandType() == SqlCommandType.SELECT, functionalName + "不支持桥接方式.");

        MappedStatement mappedStatement = configuration.getMappedStatement(statementId);
        SqlSource sqlSource = mappedStatement.getSqlSource();
        Object params = parameterMap.get(MapperParam.PARAMS);
        BoundSql originalBoundSql = sqlSource.getBoundSql(params);

        Dialect dialect = DialectContext.getDefaultDialect();
        if (dialect instanceof RuntimeDialect) {
            if (DialectContext.isDominate()) {
                dialect = DialectContext.getDialect();
            } else {
                dialect = dialectFactory.getDialect(mappedStatement);
            }
        }

        String sql = originalBoundSql.getSql();
        if (adapterType == ProviderAdapter.Type.COUNT) {
            String countColumn = originalBoundSql.hasAdditionalParameter(Constants.COUNT_COLUMN)
                    ? String.valueOf(originalBoundSql.getAdditionalParameter(Constants.COUNT_COLUMN)) : null;
            String countStr = dialect.optimizationCountSql(sql, countColumn);
            boundSql = createNewBoundSql(configuration, countStr, originalBoundSql);
        } else if (adapterType == ProviderAdapter.Type.PAGE) {
            Page<?> page = SelectPageMapper.PageStorage.getPage();
            if (page == null) {
                boundSql = originalBoundSql;
            } else {
                sql = sql.endsWith(SQLConstants.FOR_UPDATE) ? sql.replace(SQLConstants.FOR_UPDATE, SQLConstants.EMPTY) : sql;
                String pageSql = dialect.getLimitHandler().processSql(sql, page.getOffset(), page.getLimit());
                boundSql = createNewBoundSql(configuration, pageSql, originalBoundSql);
            }
        } else if (adapterType == ProviderAdapter.Type.EXISTS) {
            String existsStr = dialect.optimizationExistsSql(sql);
            boundSql = createNewBoundSql(configuration, existsStr, originalBoundSql);
        }
        Preconditions.checkNotNull(boundSql, "未找到桥接BoundSql!");
        String[] methodParamNames = (String[]) METHOD_PARAM_NAME_CACHE.getObject(statementId);
        if (methodParamNames == null) {
            methodParamNames = getMethodParamNames(lambdaInfo);
            METHOD_PARAM_NAME_CACHE.putObject(statementId, methodParamNames);
        }

        setBoundSqlParameter(boundSql, methodParamNames, params);
        return boundSql;
    }

    private void setBoundSqlParameter(BoundSql boundSql, String[] names, Object params) {
        Object[] args = params instanceof Object[] ? (Object[]) params : new Object[]{params};
        for (int i = 0; i < args.length; i++) {
            boundSql.setAdditionalParameter(names[i], args[i]);
        }
    }

    private String[] getMethodParamNames(LambdaInfo lambdaInfo) {
        Method[] methods = lambdaInfo.getType().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(lambdaInfo.getMethodName())) {
                ParamNameResolver paramNameResolver = new ParamNameResolver(configuration, method);
                return paramNameResolver.getNames();
            }
        }
        throw new NoSugarException(lambdaInfo.getMethodName() + "方法未找到");
    }

    public static BoundSql createNewBoundSql(Configuration configuration, String sql, BoundSql originalBoundSql) {
        BoundSql boundSql = new BoundSql(configuration, sql, originalBoundSql.getParameterMappings(), originalBoundSql.getParameterObject());
        for (ParameterMapping parameterMapping : originalBoundSql.getParameterMappings()) {
            Object parameter = originalBoundSql.getAdditionalParameter(parameterMapping.getProperty());
            if (parameter == null && !boundSql.hasAdditionalParameter(parameterMapping.getProperty())) {
                continue;
            }
            boundSql.setAdditionalParameter(parameterMapping.getProperty(), parameter);
        }
        return boundSql;
    }

    private static Class<?> getTargetMapperClass(Object obj) {
        if (obj != null && Proxy.isProxyClass(obj.getClass())) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(obj);
            if (invocationHandler instanceof MapperProxy) {
                MapperProxy<?> mapperProxy = (MapperProxy<?>) invocationHandler;
                try {
                    return (Class<?>) MAPPER_INTERFACE_FIELD.get(mapperProxy);
                } catch (Exception e) {
                    throw new NoSugarException(e);
                }
            }
        }
        return null;
    }

}
