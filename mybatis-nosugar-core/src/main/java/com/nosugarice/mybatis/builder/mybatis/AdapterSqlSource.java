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

package com.nosugarice.mybatis.builder.mybatis;

import com.nosugarice.mybatis.annotation.Provider;
import com.nosugarice.mybatis.exception.NoSugarException;
import com.nosugarice.mybatis.util.LambdaUtils;
import com.nosugarice.mybatis.util.Preconditions;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.session.Configuration;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/9
 */
public class AdapterSqlSource implements SqlSource {

    protected final Configuration configuration;
    protected final Class<?> mapperInterface;
    protected final Method method;

    private static final Map<String, Method> MAPPED_STATEMENT_METHOD_MAP = new HashMap<>();

    public AdapterSqlSource(Configuration configuration, Class<?> mapperInterface, Method method) {
        this.configuration = configuration;
        this.method = method;
        this.mapperInterface = mapperInterface;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        BoundSql boundSql = null;
        if (parameterObject instanceof Map) {
            Map<?, ?> parameterMap = ((Map<?, ?>) parameterObject);
            Object mapperBiFunction = parameterMap.get("mapperBiFunction");
            Object params = parameterMap.get("params");
            String functionalName = LambdaUtils.getFunctionalName((Serializable) mapperBiFunction);
            String mappedStatementId = mapperInterface.getName() + "." + functionalName;
            boolean hasStatement = configuration.hasStatement(mappedStatementId);
            Method method = MAPPED_STATEMENT_METHOD_MAP.get(mappedStatementId);
            Provider.Adapter adapter = Optional.ofNullable(method)
                    .map(methodTemp -> methodTemp.getAnnotation(Provider.class))
                    .map(Provider::adapter)
                    .orElseThrow(() -> new NoSugarException("未设置桥接方式!"));
            if (hasStatement && adapter != null) {
                MappedStatement mappedStatement = configuration.getMappedStatement(mappedStatementId);
                SqlSource sqlSource = mappedStatement.getSqlSource();
                //暂时只有count
                if (adapter == Provider.Adapter.COUNT) {
                    if (sqlSource != null) {
                        BoundSql oldBoundSql = sqlSource.getBoundSql(parameterObject);
                        String countStr = "SELECT COUNT(*) " + CountMutativeSqlSource.optimizationCountSql(oldBoundSql.getSql());
                        boundSql = new AdapterBoundSql(configuration, countStr, oldBoundSql.getParameterMappings(), params, method);
                    }
                }
            }
        }
        Preconditions.checkNotNull(boundSql, "未找到桥接BoundSql!");
        return boundSql;
    }

    public static void registerAdapterMethod(String mappedStatementId, Method method) {
        MAPPED_STATEMENT_METHOD_MAP.put(mappedStatementId, method);
    }

    public static class AdapterBoundSql extends BoundSql {

        private final Configuration configuration;
        private final Method adapterMethod;

        public AdapterBoundSql(Configuration configuration, String sql, List<ParameterMapping> parameterMappings
                , Object parameterObject, Method adapterMethod) {
            super(configuration, sql, parameterMappings, parameterObject);
            this.configuration = configuration;
            this.adapterMethod = adapterMethod;
            init();
        }

        private void init() {
            ParamNameResolver paramNameResolver = new ParamNameResolver(configuration, adapterMethod);
            String[] names = paramNameResolver.getNames();
            Object[] args = super.getParameterObject() instanceof Object[] ? (Object[]) super.getParameterObject() : new Object[]{super.getParameterObject()};
            for (int i = 0; i < args.length; i++) {
                setAdditionalParameter(names[i], args[i]);
            }
        }

    }

}
