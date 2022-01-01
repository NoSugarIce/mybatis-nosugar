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

package com.nosugarice.mybatis.builder.mapper;

import com.nosugarice.mybatis.annotation.Provider;
import com.nosugarice.mybatis.sqlsource.CountProviderSqlSource;
import com.nosugarice.mybatis.sqlsource.ExistsProviderSqlSource;
import com.nosugarice.mybatis.sqlsource.ProviderSqlSource;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlSource;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/5
 */
public class ProviderMapperBuilder extends AbstractMapperBuilder<ProviderMapperBuilder> {

    @Override
    public boolean isMatchMapper(Class<?> mapperType) {
        return true;
    }

    @Override
    public boolean isMatch(Method method) {
        return method.isAnnotationPresent(Provider.class) && notHasStatement(method);
    }

    @Override
    public void process(Method method) {
        if (isCountMethod(method)) {
            processCountMethod(method);
        }
        if (isExistsMethod(method)) {
            processExistsMethod(method);
        }
    }

    private boolean isCountMethod(Method method) {
        return Optional.of(method.getAnnotation(Provider.class)).map(Provider::value).orElse(null) == Provider.Type.COUNT;
    }

    private boolean isExistsMethod(Method method) {
        return Optional.of(method.getAnnotation(Provider.class)).map(Provider::value).orElse(null) == Provider.Type.EXISTS;
    }

    private void processCountMethod(Method method) {
        String methodMappedStatementId = getMethodMappedStatementId(method);
        ProviderSqlSource sqlSource = new CountProviderSqlSource(configuration, mapperClass, method);
        MappedStatement mappedStatement = sqlSource.getDefaultSelectMappedStatement();

        ResultMap.Builder resultMapBuilder = new ResultMap.Builder(configuration, methodMappedStatementId + "-Inline"
                , Long.class, new ArrayList<>());
        List<ResultMap> resultMaps = new ArrayList<>();
        resultMaps.add(resultMapBuilder.build());

        addMappedStatement(methodMappedStatementId, resultMaps, sqlSource, mappedStatement);
    }

    private void processExistsMethod(Method method) {
        String methodMappedStatementId = getMethodMappedStatementId(method);
        ProviderSqlSource sqlSource = new ExistsProviderSqlSource(configuration, mapperClass, method, buildingContext.getDialect());
        MappedStatement mappedStatement = sqlSource.getDefaultSelectMappedStatement();

        ResultMap.Builder resultMapBuilder = new ResultMap.Builder(configuration, methodMappedStatementId + "-Inline"
                , Integer.class, new ArrayList<>());
        List<ResultMap> resultMaps = new ArrayList<>();
        resultMaps.add(resultMapBuilder.build());

        addMappedStatement(methodMappedStatementId, resultMaps, sqlSource, mappedStatement);
    }

    private void addMappedStatement(String methodMappedStatementId, List<ResultMap> resultMaps, SqlSource sqlSource
            , MappedStatement originalMappedStatement) {
        MappedStatement newMappedStatement = new MappedStatement.Builder(configuration, methodMappedStatementId
                , sqlSource
                , originalMappedStatement.getSqlCommandType())
                .resultMaps(resultMaps)
                .resource(originalMappedStatement.getResource())
                .fetchSize(originalMappedStatement.getFetchSize())
                .statementType(originalMappedStatement.getStatementType())
                .keyGenerator(originalMappedStatement.getKeyGenerator())
                .timeout(originalMappedStatement.getTimeout())
                .parameterMap(originalMappedStatement.getParameterMap())
                .resultSetType(originalMappedStatement.getResultSetType())
                .cache(originalMappedStatement.getCache())
                .flushCacheRequired(originalMappedStatement.isFlushCacheRequired())
                .build();
        configuration.addMappedStatement(newMappedStatement);
    }

    @Override
    public int getOrder() {
        return 20;
    }
}
