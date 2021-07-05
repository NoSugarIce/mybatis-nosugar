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

package com.nosugarice.mybatis.builder;

import com.nosugarice.mybatis.annotation.Provider;
import com.nosugarice.mybatis.annotation.ProviderAdapter;
import com.nosugarice.mybatis.builder.mybatis.AdapterSqlSource;
import com.nosugarice.mybatis.builder.mybatis.CountMutativeSqlSource;
import com.nosugarice.mybatis.builder.mybatis.MutativeSqlSource;
import com.nosugarice.mybatis.builder.mybatis.PageMutativeSqlSource;
import com.nosugarice.mybatis.builder.statement.BaseMapperStatementBuilder;
import com.nosugarice.mybatis.config.MetadataBuildingContext;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMap;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.reflection.MetaObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/5
 */
public class MutativeSqlBuilder extends AbstractMapperBuilder {

    public MutativeSqlBuilder(MetadataBuildingContext buildingContext, Class<?> mapperInterface) {
        super(buildingContext, mapperInterface);
    }

    @Override
    public boolean isNeedAchieveMethod(Method method) {
        return isCountMethod(method) || isPageMethod(method) || isProviderAdapterMethod(method) || isAdapterMethod(method);
    }

    @Override
    public void checkBeforeProcessMethod(Method method) {
    }

    @Override
    public void processMethod(Method method) {
        if (isCountMethod(method)) {
            processCountMethod(method);
        }
        if (isPageMethod(method)) {
            processPageMethod(method);
        }
        if (isProviderAdapterMethod(method)) {
            processProviderAdapterMethod(method);
        }
        if (isAdapterMethod(method)) {
            processAdapterMethod(method);
        }
    }

    private boolean isCountMethod(Method method) {
        return (Optional.ofNullable(method.getAnnotation(Provider.class)).map(Provider::value).orElse(null) == Provider.Type.COUNT
                || method.getName().startsWith("countWith"))
                && !hasStatement(method);
    }

    private boolean isPageMethod(Method method) {
        return Optional.ofNullable(method.getAnnotation(Provider.class)).map(Provider::value).orElse(null) == Provider.Type.PAGE;
    }

    private boolean isProviderAdapterMethod(Method method) {
        return method.getAnnotation(ProviderAdapter.class) != null;
    }

    private boolean isAdapterMethod(Method method) {
        return Optional.ofNullable(method.getAnnotation(Provider.class)).map(Provider::adapter).orElse(Provider.Adapter.NONE) != Provider.Adapter.NONE;
    }

    private void processCountMethod(Method method) {
        String methodMappedStatementId = getMethodMappedStatementId(method);
        MutativeSqlSource sqlSource = new CountMutativeSqlSource(configuration, mapperInterface, method);
        MappedStatement mappedStatement = sqlSource.getDefaultSelectMappedStatement();

        ResultMap.Builder resultMapBuilder = new ResultMap.Builder(configuration, methodMappedStatementId + "-Inline"
                , Long.class, new ArrayList<>());
        List<ResultMap> resultMaps = new ArrayList<>();
        resultMaps.add(resultMapBuilder.build());

        MappedStatement newMappedStatement = new MappedStatement.Builder(configuration, methodMappedStatementId
                , sqlSource
                , mappedStatement.getSqlCommandType())
                .resultMaps(resultMaps)
                .resource(mappedStatement.getResource())
                .fetchSize(mappedStatement.getFetchSize())
                .statementType(mappedStatement.getStatementType())
                .keyGenerator(mappedStatement.getKeyGenerator())
                .timeout(mappedStatement.getTimeout())
                .parameterMap(mappedStatement.getParameterMap())
                .resultSetType(mappedStatement.getResultSetType())
                .cache(mappedStatement.getCache())
                .flushCacheRequired(mappedStatement.isFlushCacheRequired())
                .useCache(mappedStatement.isUseCache())
                .build();
        configuration.addMappedStatement(newMappedStatement);
    }

    private void processPageMethod(Method method) {
        String methodMappedStatementId = getMethodMappedStatementId(method);
        MappedStatement mappedStatement = configuration.getMappedStatement(methodMappedStatementId);
        if (mappedStatement != null && !PageMutativeSqlSource.class.isAssignableFrom(mappedStatement.getSqlSource().getClass())) {
            MutativeSqlSource sqlSource = new PageMutativeSqlSource(configuration, buildingContext.getDialect(), mapperInterface, method);
            MetaObject metaObject = configuration.newMetaObject(mappedStatement);
            metaObject.setValue("sqlSource", sqlSource);
        }
    }

    private void processProviderAdapterMethod(Method method) {
        String methodMappedStatementId = getMethodMappedStatementId(method);

        SqlSource sqlSource = new AdapterSqlSource(configuration, mapperInterface, method);

        ResultMap.Builder resultMapBuilder = new ResultMap.Builder(configuration, methodMappedStatementId + "-Inline"
                , Object.class, new ArrayList<>());
        List<ResultMap> resultMaps = new ArrayList<>();
        resultMaps.add(resultMapBuilder.build());

        Class<?> parameterTypeClass = BaseMapperStatementBuilder.getParameterType(method);

        List<ParameterMapping> parameterMappings = new ArrayList<>();
        ParameterMap parameterMap = new ParameterMap.Builder(configuration, methodMappedStatementId + "-Inline"
                , parameterTypeClass, parameterMappings)
                .build();

        MappedStatement newMappedStatement = new MappedStatement.Builder(configuration, methodMappedStatementId
                , sqlSource
                , SqlCommandType.SELECT)
                .resultMaps(resultMaps)
                .statementType(StatementType.PREPARED)
                .keyGenerator(null)
                .parameterMap(parameterMap)
                .resultSetType(configuration.getDefaultResultSetType())
                .build();
        configuration.addMappedStatement(newMappedStatement);
    }

    private void processAdapterMethod(Method method) {
        String methodMappedStatementId = getMethodMappedStatementId(method);
        AdapterSqlSource.registerAdapterMethod(methodMappedStatementId, method);
    }

}
