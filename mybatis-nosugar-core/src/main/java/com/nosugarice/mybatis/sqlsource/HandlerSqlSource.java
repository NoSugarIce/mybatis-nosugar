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

import com.nosugarice.mybatis.config.EntityMetadata;
import com.nosugarice.mybatis.config.MetadataBuildingContext;
import com.nosugarice.mybatis.handler.ParameterValueHandler;
import com.nosugarice.mybatis.handler.ValueHandler;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.sql.ParameterBind.ParameterColumnBind;
import com.nosugarice.mybatis.sql.SqlAndParameterBind;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.type.TypeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/6/12
 */
public abstract class HandlerSqlSource implements SqlSource {

    protected final SqlCommandType sqlCommandType;
    protected final MetadataBuildingContext buildingContext;
    protected final String sql;

    private List<Function<String, String>> sqlHandlers;

    private static final Map<Integer, ParameterValueHandler<?>> HANDLER_CACHE = new HashMap<>();

    protected HandlerSqlSource(SqlCommandType sqlCommandType, MetadataBuildingContext buildingContext, String sql) {
        this.sqlCommandType = sqlCommandType;
        this.buildingContext = buildingContext;
        this.sql = sql;
    }

    public void addSqlHandler(Function<String, String> sqlHandler) {
        if (sqlHandlers == null) {
            sqlHandlers = new ArrayList<>();
        }
        sqlHandlers.add(sqlHandler);
    }

    public String sqlHandler(String sql) {
        if (sqlHandlers != null && sql != null) {
            for (Function<String, String> sqlHandler : sqlHandlers) {
                sql = sqlHandler.apply(sql);
            }
        }
        return sql;
    }

    public List<ParameterMapping> getParameterMappings(SqlAndParameterBind sqlAndParameterBind) {
        List<ParameterMapping> parameterMappings = new ArrayList<>();
        for (ParameterColumnBind parameterColumnBind : sqlAndParameterBind.getParameterBind().getParameterColumnBinds()) {
            EntityMetadata entityMetadata = buildingContext.getEntityMetadata(parameterColumnBind.getEntityClass());
            RelationalProperty property = entityMetadata.getPropertyByColumnName(parameterColumnBind.getColumn());
            Class<?> propertyType = Object.class;
            Class<? extends TypeHandler<?>> typeHandlerType = null;
            if (property != null) {
                propertyType = property.getJavaType();
                typeHandlerType = property.getTypeHandler();
            }
            ParameterMapping.Builder builder = new ParameterMapping.Builder(buildingContext.getConfiguration()
                    , parameterColumnBind.getParameter(), propertyType);
            if (typeHandlerType != null) {
                builder.typeHandler(buildingContext.getConfiguration().getTypeHandlerRegistry().getTypeHandler(typeHandlerType));
            }
            ParameterMapping parameterMapping = builder.build();
            if (property != null && parameterColumnBind.isCanHandle()) {
                ValueHandler<?> valueHandler = null;
                if (sqlCommandType == SqlCommandType.INSERT) {
                    valueHandler = property.getValue().insertHandler();
                } else if (sqlCommandType == SqlCommandType.UPDATE) {
                    valueHandler = property.getValue().updateHandler();
                }
                if (valueHandler != null) {
                    builder.typeHandler(createTypeHandler(parameterMapping.getTypeHandler(), valueHandler));
                }
            }
            parameterMappings.add(parameterMapping);
        }
        return parameterMappings;
    }

    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    private static ParameterValueHandler<?> createTypeHandler(TypeHandler<?> typeHandler, ValueHandler<?> valueHandler) {
        return HANDLER_CACHE.computeIfAbsent(Objects.hash(typeHandler, valueHandler), k -> new ParameterValueHandler(typeHandler, valueHandler));
    }

    protected static Object[] getOriginalParameters(Object parameterObject, String[] parameterNames) {
        if (parameterObject instanceof MapperMethod.ParamMap) {
            if (parameterNames.length == 1) {
                return new Object[]{((MapperMethod.ParamMap<?>) parameterObject).get(parameterNames[0])};
            } else {
                Object[] params = new Object[parameterNames.length];
                for (int i = 0; i < parameterNames.length; i++) {
                    params[i] = ((MapperMethod.ParamMap<?>) parameterObject).get(parameterNames[i]);
                }
                return params;
            }
        }
        return new Object[]{parameterObject};
    }

}
