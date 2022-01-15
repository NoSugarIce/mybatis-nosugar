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

import com.nosugarice.mybatis.annotation.ProviderAdapter;
import com.nosugarice.mybatis.mapper.function.AdapterMapper;
import com.nosugarice.mybatis.sqlsource.AdapterSqlSource;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMap;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/5
 */
public class AdapterMapperBuilder extends AbstractMapperBuilder {

    @Override
    public boolean supportMapper(Class<?> mapperType) {
        return AdapterMapper.class.isAssignableFrom(mapperType);
    }

    @Override
    public boolean supportMethod(Method method) {
        return method.isAnnotationPresent(ProviderAdapter.class);
    }

    @Override
    public void process(Method method) {
        ProviderAdapter providerAdapter = method.getAnnotation(ProviderAdapter.class);

        Integer cacheHash = Objects.hash(configuration, providerAdapter.value());
        SqlSource sqlSource = buildingContext.getByCache(cacheHash);
        if (sqlSource == null) {
            sqlSource = new AdapterSqlSource(configuration, providerAdapter.value(), buildingContext.getDialect());
            buildingContext.cacheObject(cacheHash, sqlSource);
        }

        String methodMappedStatementId = getMethodMappedStatementId(method);
        ResultMap.Builder resultMapBuilder = new ResultMap.Builder(configuration, methodMappedStatementId + "-Inline"
                , Object.class, new ArrayList<>());
        List<ResultMap> resultMaps = new ArrayList<>();
        resultMaps.add(resultMapBuilder.build());

        List<ParameterMapping> parameterMappings = new ArrayList<>();
        ParameterMap parameterMap = new ParameterMap.Builder(configuration, methodMappedStatementId + "-Inline"
                , MapperMethod.ParamMap.class, parameterMappings)
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

    @Override
    public int getOrder() {
        return 30;
    }
}
