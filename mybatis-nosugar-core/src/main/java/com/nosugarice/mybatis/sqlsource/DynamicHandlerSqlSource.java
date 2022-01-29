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

import com.nosugarice.mybatis.builder.SqlSourceScriptBuilder;
import com.nosugarice.mybatis.config.EntityMetadata;
import com.nosugarice.mybatis.config.MetadataBuildingContext;
import com.nosugarice.mybatis.handler.ParameterValueHandler;
import com.nosugarice.mybatis.handler.ValueHandler;
import com.nosugarice.mybatis.mapper.function.FunS;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.sql.ParameterBind.ParameterColumnBind;
import com.nosugarice.mybatis.sql.SqlAndParameterBind;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/10/30
 */
public class DynamicHandlerSqlSource implements SqlSource {

    private static final Map<Class<?>, JdbcType> JDBC_TYPE_MAP;

    private final SqlCommandType sqlCommandType;

    private final MetadataBuildingContext buildingContext;

    private final String[] parameterNames;

    private final FunS<SqlAndParameterBind> providerFun;

    private final SqlSourceScriptBuilder sqlSourceScriptBuilder;

    private final boolean fixedParameter;

    private SqlAndParameterBind sqlAndParameterBindCache;

    private List<ParameterMapping> parameterMappingsCache;

    private List<Function<String, String>> sqlHandlers;

    private static final Map<Integer, ParameterValueHandler<?>> HANDLER_CACHE = new HashMap<>();

    private static final Map<ParameterColumnBind, ParameterColumnBind> PARAMETER_COLUMN_BIND_CACHE = new HashMap<>();

    public DynamicHandlerSqlSource(SqlCommandType sqlCommandType, MetadataBuildingContext buildingContext, String[] parameterNames
            , FunS<SqlAndParameterBind> providerFun, SqlSourceScriptBuilder sqlSourceScriptBuilder, boolean fixedParameter) {
        this.sqlCommandType = sqlCommandType;
        this.buildingContext = buildingContext;
        this.parameterNames = parameterNames;
        this.providerFun = providerFun;
        this.sqlSourceScriptBuilder = sqlSourceScriptBuilder;
        this.fixedParameter = fixedParameter;
    }

    static {
        JDBC_TYPE_MAP = new HashMap<>(27);
        JDBC_TYPE_MAP.put(BigDecimal.class, JdbcType.NUMERIC);
        JDBC_TYPE_MAP.put(BigInteger.class, JdbcType.BIGINT);
        JDBC_TYPE_MAP.put(boolean.class, JdbcType.BOOLEAN);
        JDBC_TYPE_MAP.put(Boolean.class, JdbcType.BOOLEAN);
        JDBC_TYPE_MAP.put(byte[].class, JdbcType.VARBINARY);
        JDBC_TYPE_MAP.put(byte.class, JdbcType.TINYINT);
        JDBC_TYPE_MAP.put(Byte.class, JdbcType.TINYINT);
        JDBC_TYPE_MAP.put(Calendar.class, JdbcType.TIMESTAMP);
        JDBC_TYPE_MAP.put(java.sql.Date.class, JdbcType.DATE);
        JDBC_TYPE_MAP.put(java.util.Date.class, JdbcType.TIMESTAMP);
        JDBC_TYPE_MAP.put(double.class, JdbcType.DOUBLE);
        JDBC_TYPE_MAP.put(Double.class, JdbcType.DOUBLE);
        JDBC_TYPE_MAP.put(float.class, JdbcType.REAL);
        JDBC_TYPE_MAP.put(Float.class, JdbcType.REAL);
        JDBC_TYPE_MAP.put(int.class, JdbcType.INTEGER);
        JDBC_TYPE_MAP.put(Integer.class, JdbcType.INTEGER);
        JDBC_TYPE_MAP.put(LocalDate.class, JdbcType.DATE);
        JDBC_TYPE_MAP.put(LocalDateTime.class, JdbcType.TIMESTAMP);
        JDBC_TYPE_MAP.put(LocalTime.class, JdbcType.TIME);
        JDBC_TYPE_MAP.put(long.class, JdbcType.BIGINT);
        JDBC_TYPE_MAP.put(Long.class, JdbcType.BIGINT);
        JDBC_TYPE_MAP.put(OffsetDateTime.class, JdbcType.TIMESTAMP_WITH_TIMEZONE);
        JDBC_TYPE_MAP.put(OffsetTime.class, JdbcType.TIME_WITH_TIMEZONE);
        JDBC_TYPE_MAP.put(Short.class, JdbcType.SMALLINT);
        JDBC_TYPE_MAP.put(String.class, JdbcType.VARCHAR);
        JDBC_TYPE_MAP.put(Time.class, JdbcType.TIME);
        JDBC_TYPE_MAP.put(Timestamp.class, JdbcType.TIMESTAMP);
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

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        if (fixedParameter) {
            if (sqlAndParameterBindCache == null) {
                SqlAndParameterBind sqlAndParameterBind = sqlSourceScriptBuilder.build(providerFun, SqlSourceScriptBuilder.PLACEHOLDER_OBJECT);
                sqlAndParameterBindCache = createBindCache(sqlAndParameterBind);
                parameterMappingsCache = getParameterMappings(sqlAndParameterBind.getParameterBind().getParameterColumnBinds());
            }
            BoundSql boundSql = new BoundSql(buildingContext.getConfiguration(), sqlHandler(sqlAndParameterBindCache.getSql())
                    , parameterMappingsCache, parameterObject);
            sqlAndParameterBindCache.getParameterHandle()
                    .apply(parameterObject, sqlAndParameterBindCache.getParameterBind().getParameterColumnBinds(), boundSql);
            return boundSql;
        } else {
            Object[] params = getOriginalParameters(parameterObject, parameterNames);
            SqlAndParameterBind sqlAndParameterBind = sqlSourceScriptBuilder.build(providerFun, params);
            List<ParameterMapping> parameterMappings = getParameterMappings(sqlAndParameterBind.getParameterBind().getParameterColumnBinds());
            BoundSql boundSql = new BoundSql(buildingContext.getConfiguration(), sqlHandler(sqlAndParameterBind.getSql())
                    , parameterMappings, parameterObject);
            sqlAndParameterBind.getParameterBind().getParameterColumnBinds()
                    .forEach(parameterColumnBind
                            -> boundSql.setAdditionalParameter(parameterColumnBind.getParameter(), parameterColumnBind.getValue()));
            return boundSql;
        }
    }

    public List<ParameterMapping> getParameterMappings(List<ParameterColumnBind> parameterColumnBinds) {
        List<ParameterMapping> parameterMappings = new ArrayList<>();
        for (ParameterColumnBind parameterColumnBind : parameterColumnBinds) {
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
            builder.jdbcType(JDBC_TYPE_MAP.get(propertyType));
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
        } else if (parameterObject instanceof Object[]) {
            return (Object[]) parameterObject;
        }
        return new Object[]{parameterObject};
    }


    private SqlAndParameterBind createBindCache(SqlAndParameterBind sqlAndParameterBind) {
        List<ParameterColumnBind> parameterColumnBinds = sqlAndParameterBind.getParameterBind().getParameterColumnBinds();
        for (int i = 0; i < parameterColumnBinds.size(); i++) {
            ParameterColumnBind parameterColumnBind = parameterColumnBinds.get(i);
            if (PARAMETER_COLUMN_BIND_CACHE.containsKey(parameterColumnBind)) {
                parameterColumnBinds.set(i, PARAMETER_COLUMN_BIND_CACHE.get(parameterColumnBind));
            } else {
                PARAMETER_COLUMN_BIND_CACHE.put(parameterColumnBind, parameterColumnBind);
            }
        }
        return sqlAndParameterBind;
    }

}
