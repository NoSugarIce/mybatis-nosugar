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

import com.nosugarice.mybatis.builder.sql.SqlScriptBuilder;
import com.nosugarice.mybatis.builder.statement.BaseMapperStatementBuilder;
import com.nosugarice.mybatis.builder.statement.MapperStatementFactory;
import com.nosugarice.mybatis.config.MetadataBuildingContext;
import com.nosugarice.mybatis.mapper.delete.DeleteMapper;
import com.nosugarice.mybatis.mapper.insert.InsertMapper;
import com.nosugarice.mybatis.mapper.logicdelete.LogicDeleteMapper;
import com.nosugarice.mybatis.mapper.select.SelectMapper;
import com.nosugarice.mybatis.mapper.update.UpdateMapper;
import com.nosugarice.mybatis.sql.SqlBuilder;
import org.apache.ibatis.builder.MapperBuilderAssistant;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/11/15
 */
public class MapperEntityStatementBuilder extends AbstractMapperBuilder {

    private static final Set<Class<?>> DEFAULT_MAPPER_CLASSES = Stream.of(SelectMapper.class, InsertMapper.class
            , UpdateMapper.class, DeleteMapper.class, LogicDeleteMapper.class).collect(Collectors.toSet());

    private final Map<Class<?>, BaseMapperStatementBuilder> mapperStatementBuilderMap = new HashMap<>();

    public MapperEntityStatementBuilder(MetadataBuildingContext buildingContext, Class<?> mapperInterface) {
        super(buildingContext, mapperInterface);
    }

    @Override
    public boolean isNeedAchieveMethod(Method method) {
        return DEFAULT_MAPPER_CLASSES.stream()
                .anyMatch(mapperClass -> mapperClass.isAssignableFrom(method.getDeclaringClass()))
                && !configuration.hasStatement(getMethodMappedStatementId(method))
                && method.isAnnotationPresent(SqlBuilder.class);
    }

    @Override
    public void checkBeforeProcessMethod(Method method) {
        MapperMetadata mapperMetadata = buildingContext.getMapperMetadata(mapperInterface);
        mapperMetadata.getSupports().checkMethod(method);
    }

    @Override
    public void processMethod(Method method) {
        MapperBuilderAssistant assistant = buildingContext.getMapperBuilderAssistant(mapperInterface);
        SqlScriptBuilder sqlScriptBuilder = buildingContext.getSqlScriptBuilder(mapperInterface);
        BaseMapperStatementBuilder statementBuilder = mapperStatementBuilderMap.computeIfAbsent(method.getDeclaringClass()
                , mapperClass -> MapperStatementFactory.getMapperStatementBuilder(mapperClass, sqlScriptBuilder, assistant));
        statementBuilder.addMappedStatement(method);
    }

}
