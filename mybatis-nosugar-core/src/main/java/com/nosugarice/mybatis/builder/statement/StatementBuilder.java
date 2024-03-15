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

package com.nosugarice.mybatis.builder.statement;

import com.nosugarice.mybatis.config.DmlType;
import com.nosugarice.mybatis.config.EntityMetadata;
import com.nosugarice.mybatis.config.MetadataBuildingContext;
import com.nosugarice.mybatis.dialect.Dialect;
import com.nosugarice.mybatis.domain.Page;
import com.nosugarice.mybatis.handler.ResultValueHandler;
import com.nosugarice.mybatis.handler.ValueHandler;
import com.nosugarice.mybatis.mapper.delete.DeleteMapper;
import com.nosugarice.mybatis.mapper.function.JpaMapper;
import com.nosugarice.mybatis.mapper.insert.InsertMapper;
import com.nosugarice.mybatis.mapper.logicdelete.LogicDeleteMapper;
import com.nosugarice.mybatis.mapper.update.UpdateMapper;
import com.nosugarice.mybatis.mapping.RelationalEntity;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.sql.SQLPart;
import com.nosugarice.mybatis.sql.SqlAndParameterBind;
import com.nosugarice.mybatis.sqlsource.SqlSourceScriptBuilder;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/2
 */
public class StatementBuilder {

    protected Class<?> mapperClass;
    protected MetadataBuildingContext buildingContext;
    protected Configuration configuration;
    protected EntityMetadata entityMetadata;
    protected SqlSourceScriptBuilder sqlSourceScriptBuilder;
    protected MapperBuilderAssistant assistant;

    StatementBuilder() {
    }

    public static StatementBuilder of(Class<?> mapperType) {
        StatementBuilder statementBuilder = null;
        if (InsertMapper.class.isAssignableFrom(mapperType)) {
            statementBuilder = new InsertStatementBuilder();
        }
        if (UpdateMapper.class.isAssignableFrom(mapperType)) {
            statementBuilder = new UpdateStatementBuilder();
        }
        if (DeleteMapper.class.isAssignableFrom(mapperType)) {
            statementBuilder = new DeleteStatementBuilder();
        }
        if (LogicDeleteMapper.class.isAssignableFrom(mapperType)) {
            statementBuilder = new LogicDeleteStatementBuilder();
        }
        if (JpaMapper.class.isAssignableFrom(mapperType)) {
            statementBuilder = new JpaStatementBuilder();
        }
        if (statementBuilder == null) {
            statementBuilder = new StatementBuilder();
        }
        return statementBuilder;
    }

    public StatementBuilder withBuilding(MetadataBuildingContext buildingContext, Class<?> mapperInterface) {
        this.buildingContext = buildingContext;
        this.mapperClass = mapperInterface;
        this.configuration = buildingContext.getConfiguration();
        this.entityMetadata = buildingContext.getEntityMetadataByMapper(mapperClass);
        this.sqlSourceScriptBuilder = buildingContext.getSqlScriptBuilderByMapper(mapperClass);
        this.assistant = buildingContext.getMapperBuilderAssistant(mapperClass);
        return this;
    }

    /**
     * Sql 执行类型
     *
     * @param method
     * @return
     */
    public DmlType getDmlType(Method method) {
        return DmlType.SELECT;
    }

    public void addMappedStatement(Method method, SqlAndParameterBind sqlAndParameterBind) {
        String mappedStatementId = mapperClass.getName() + "." + method.getName();
        if (configuration.hasStatement(mappedStatementId)) {
            return;
        }
        Class<?> parameterTypeClass = getParameterType(method);

        StatementType statementType = StatementType.PREPARED;
        DmlType dmlType = getDmlType(method);
        final SqlCommandType sqlCommandType = dmlType.getSqlCommandType();
        SqlSource sqlSource = createSqlSource(configuration, method, sqlAndParameterBind, parameterTypeClass, dmlType);

        boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
        boolean flushCache = !isSelect;

        Integer fetchSize = null;
        Integer timeout = null;

        KeyGenerator keyGenerator = getKeyGenerator(mapperClass.getName());

        ResultSetType resultSetType = configuration.getDefaultResultSetType();

        String databaseId = configuration.getDatabaseId();

        Class<?> returnType = getReturnType(method);
        String resultMapId = getResultMapId(method);
        if (resultMapId == null && isSelect) {
            Class<?> entityClass = entityMetadata.getEntityClass();
            if (returnType == entityClass) {
                returnType = null;
                resultMapId = getDefaultResultMapId();
            } else if (returnType == void.class) {
                boolean hasResultHandler = Arrays.stream(method.getParameterTypes()).anyMatch(aClass -> aClass == ResultHandler.class);
                if (hasResultHandler) {
                    returnType = null;
                    resultMapId = getDefaultResultMapId();
                    Dialect dialect = buildingContext.getDialect();
                    fetchSize = dialect.streamingResultSetFetchSize();
                }
            }
        }

        assistant.addMappedStatement(mappedStatementId, sqlSource, statementType, sqlCommandType, fetchSize, timeout
                , getParameterMapId(method), parameterTypeClass, resultMapId, returnType, resultSetType, flushCache
                , false, false, keyGenerator, getKeyProperty(), getKeyColumn(), databaseId,
                configuration.getLanguageRegistry().getDefaultDriver(), null);
    }

    protected SqlSource createSqlSource(Configuration configuration, Method method
            , SqlAndParameterBind sqlAndParameterBind, Class<?> parameterType
            , DmlType dmlType) {
        if (sqlAndParameterBind == null || sqlAndParameterBind.hasParameterHandle()) {
            return sqlSourceScriptBuilder.buildSqlSource(method, dmlType, sqlAndParameterBind, parameterType);
        }
        return configuration.getLanguageRegistry().getDefaultDriver()
                .createSqlSource(configuration, SQLPart.script(sqlAndParameterBind.getSql()), parameterType);
    }


    public KeyGenerator getKeyGenerator(String mapperName) {
        return NoKeyGenerator.INSTANCE;
    }

    public String getKeyProperty() {
        return null;
    }

    public String getKeyColumn() {
        return null;
    }

    public String getParameterMapId(Method method) {
        return null;
    }

    public String getResultMapId(Method method) {
        return null;
    }

    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    private String getDefaultResultMapId() {
        RelationalEntity relationalEntity = entityMetadata.getRelationalEntity();
        String resultMapId = mapperClass.getName() + "." + entityMetadata.getEntityClass().getName() + ".resultMap";
        if (configuration.hasResultMap(resultMapId)) {
            return resultMapId;
        }

        List<ResultMapping> resultMappings = new ArrayList<>();
        for (RelationalProperty property : relationalEntity.getProperties()) {
            ResultMapping.Builder builder = new ResultMapping.Builder(configuration, property.getName()
                    , property.getName(), property.getJavaType());
            if (property.isPrimaryKey()) {
                builder.flags(Collections.singletonList(ResultFlag.ID)).build();
            } else {
                if (property.getJdbcType() != null) {
                    builder.jdbcType(JdbcType.forCode(property.getJdbcType()));
                }
                if (property.getTypeHandler() != null) {
                    TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                    TypeHandler<?> typeHandler = typeHandlerRegistry.getMappingTypeHandler(property.getTypeHandler());
                    if (typeHandler == null) {
                        typeHandler = typeHandlerRegistry.getInstance(property.getJavaType(), property.getTypeHandler());
                        typeHandlerRegistry.register(typeHandler);
                        builder.typeHandler(typeHandler);
                    }
                }
            }
            ResultMapping resultMapping = builder.build();
            ValueHandler<?> resultHandler;
            if ((resultHandler = property.getValue().resultHandler()) != null) {
                builder.typeHandler(new ResultValueHandler(resultMapping.getTypeHandler(), resultHandler));
            }
            resultMappings.add(resultMapping);
        }

        ResultMap.Builder resultMapBuilder = new ResultMap.Builder(configuration, resultMapId
                , entityMetadata.getEntityClass(), resultMappings);
        configuration.addResultMap(resultMapBuilder.build());
        return resultMapId;
    }

    /**
     * org.apache.ibatis.builder.annotation.MapperAnnotationBuilder#getParameterType(java.lang.reflect.Method)
     *
     * @param method
     * @return
     */
    public static Class<?> getParameterType(Method method) {
        Class<?> parameterType = null;
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> currentParameterType : parameterTypes) {
            if (!Page.class.isAssignableFrom(currentParameterType)
                    && !RowBounds.class.isAssignableFrom(currentParameterType)
                    && !ResultHandler.class.isAssignableFrom(currentParameterType)) {
                if (parameterType == null) {
                    parameterType = currentParameterType;
                } else {
                    parameterType = MapperMethod.ParamMap.class;
                }
            }
        }
        return parameterType;
    }

    /**
     * org.apache.ibatis.builder.annotation.MapperAnnotationBuilder#getReturnType(java.lang.reflect.Method)
     *
     * @param method
     * @return
     */
    private Class<?> getReturnType(Method method) {
        Class<?> returnType = method.getReturnType();
        Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, mapperClass);
        if (resolvedReturnType instanceof Class) {
            returnType = (Class<?>) resolvedReturnType;
            if (returnType.isArray()) {
                returnType = returnType.getComponentType();
            }
            if (void.class.equals(returnType)) {
                ResultType rt = method.getAnnotation(ResultType.class);
                if (rt != null) {
                    returnType = rt.value();
                }
            }
        } else if (resolvedReturnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) resolvedReturnType;
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            if (Collection.class.isAssignableFrom(rawType) || Cursor.class.isAssignableFrom(rawType)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length == 1) {
                    Type returnTypeParameter = actualTypeArguments[0];
                    if (returnTypeParameter instanceof Class<?>) {
                        returnType = (Class<?>) returnTypeParameter;
                    } else if (returnTypeParameter instanceof ParameterizedType) {
                        returnType = (Class<?>) ((ParameterizedType) returnTypeParameter).getRawType();
                    } else if (returnTypeParameter instanceof GenericArrayType) {
                        Class<?> componentType = (Class<?>) ((GenericArrayType) returnTypeParameter).getGenericComponentType();
                        returnType = Array.newInstance(componentType, 0).getClass();
                    }
                }
            } else if (method.isAnnotationPresent(MapKey.class) && Map.class.isAssignableFrom(rawType)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length == 2) {
                    Type returnTypeParameter = actualTypeArguments[1];
                    if (returnTypeParameter instanceof Class<?>) {
                        returnType = (Class<?>) returnTypeParameter;
                    } else if (returnTypeParameter instanceof ParameterizedType) {
                        returnType = (Class<?>) ((ParameterizedType) returnTypeParameter).getRawType();
                    }
                }
            } else if (Optional.class.equals(rawType)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Type returnTypeParameter = actualTypeArguments[0];
                if (returnTypeParameter instanceof Class<?>) {
                    returnType = (Class<?>) returnTypeParameter;
                }
            }
        }
        return returnType;
    }

    public SqlSourceScriptBuilder getSqlScriptBuilder() {
        return sqlSourceScriptBuilder;
    }
}
