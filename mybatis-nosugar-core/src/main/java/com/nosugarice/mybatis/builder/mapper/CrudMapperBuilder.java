/*
 * Copyright 2021-2023 NoSugarIce
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nosugarice.mybatis.builder.mapper;

import com.nosugarice.mybatis.builder.statement.StatementBuilder;
import com.nosugarice.mybatis.mapper.delete.DeleteMapper;
import com.nosugarice.mybatis.mapper.insert.InsertMapper;
import com.nosugarice.mybatis.mapper.logicdelete.LogicDeleteMapper;
import com.nosugarice.mybatis.mapper.select.SelectMapper;
import com.nosugarice.mybatis.mapper.update.UpdateMapper;
import com.nosugarice.mybatis.sql.SqlBuilder;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/11/15
 */
public class CrudMapperBuilder extends AbstractMapperBuilder {

    private static final Set<Class<?>> DEFAULT_MAPPER_CLASSES = Stream.of(SelectMapper.class, InsertMapper.class
            , UpdateMapper.class, DeleteMapper.class, LogicDeleteMapper.class).collect(Collectors.toSet());

    private final Map<Class<?>, StatementBuilder> mapperStatementBuilderMap = new ConcurrentHashMap<>();

    @Override
    public boolean supportMapper(Class<?> mapperType) {
        return DEFAULT_MAPPER_CLASSES.stream().anyMatch(mapperClass -> mapperClass.isAssignableFrom(mapperType));
    }

    @Override
    public boolean supportMethod(Method method) {
        return notHasStatement(method) && method.isAnnotationPresent(SqlBuilder.class);
    }

    @Override
    public void process(Method method) {
        Optional.of(method.getAnnotation(SqlBuilder.class))
                .map(SqlBuilder::sqlFunction)
                .ifPresent(sqlFunction -> buildingContext.getSqlScriptBuilderByMapper(mapperClass).bind(method, sqlFunction.providerFun()));
        mapperStatementBuilderMap.computeIfAbsent(method.getDeclaringClass()
                        , clazz -> StatementBuilder.of(clazz).withBuilding(buildingContext, mapperClass))
                .addMappedStatement(method, null);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
