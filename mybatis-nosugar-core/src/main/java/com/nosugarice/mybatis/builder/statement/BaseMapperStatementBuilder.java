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

package com.nosugarice.mybatis.builder.statement;

import com.nosugarice.mybatis.builder.sql.SqlScriptBuilder;
import com.nosugarice.mybatis.domain.Page;
import com.nosugarice.mybatis.mapper.function.Mapper;
import com.nosugarice.mybatis.mapping.RelationalEntity;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.sql.SqlBuilder;
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
import java.lang.reflect.Modifier;
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
public abstract class BaseMapperStatementBuilder {

    protected final Configuration configuration;
    protected final Class<?> mapperInterface;
    protected final SqlScriptBuilder sqlScriptBuilder;
    protected final MapperBuilderAssistant assistant;

    protected BaseMapperStatementBuilder(SqlScriptBuilder sqlScriptBuilder, MapperBuilderAssistant assistant) {
        this.configuration = sqlScriptBuilder.getConfiguration();
        this.mapperInterface = sqlScriptBuilder.getMapperInterface();
        this.sqlScriptBuilder = sqlScriptBuilder;
        this.assistant = assistant;
        init();
    }

    /**
     * 需要构建的Mapper类型
     *
     * @return
     */
    public abstract Collection<Class<? extends Mapper>> getMapperTypes();

    /**
     * Sql 执行类型
     *
     * @param method
     * @return
     */
    public abstract SqlCommandType getSqlCommandType(Method method);

    /**
     * 初始化
     */
    private void init() {
        getMapperTypes().forEach(mapperClass -> Arrays.stream(mapperClass.getMethods())
                .filter(method -> Modifier.isAbstract(method.getModifiers()))
                .filter(method -> method.isAnnotationPresent(SqlBuilder.class))
                .forEach(method -> sqlScriptBuilder.bind(method, method.getAnnotation(SqlBuilder.class).sqlSourceFunction())));
    }

    public void addMappedStatement(Method method) {
        String mappedStatementId = mapperInterface.getName() + "." + method.getName();
        if (configuration.hasStatement(mappedStatementId)) {
            return;
        }
        String sqlScript = sqlScriptBuilder.build(method);
        Class<?> parameterTypeClass = getParameterType(method);
        SqlSource sqlSource = createSqlSource(configuration, sqlScript, parameterTypeClass, method);

        StatementType statementType = StatementType.PREPARED;
        final SqlCommandType sqlCommandType = getSqlCommandType(method);
        boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
        boolean flushCache = !isSelect;

        Integer fetchSize = null;
        Integer timeout = null;

        KeyGenerator keyGenerator = getKeyGenerator(mapperInterface.getName());

        ResultSetType resultSetType = configuration.getDefaultResultSetType();

        String databaseId = configuration.getDatabaseId();

        Class<?> returnType = getReturnType(method);
        String resultMapId = getResultMapId(method);
        if (resultMapId == null && isSelect) {
            Class<?> entityClass = sqlScriptBuilder.getMapperBuildRaw().getRelationalEntity().getEntityClass().getClassType();
            if (entityClass == returnType) {
                returnType = null;
                resultMapId = getResultMapId();
            }
        }

        assistant.addMappedStatement(mappedStatementId, sqlSource, statementType, sqlCommandType, fetchSize, timeout
                , getParameterMapId(method), parameterTypeClass, resultMapId, returnType, resultSetType, flushCache
                , false, false, keyGenerator, getKeyProperty(), getKeyColumn(), databaseId,
                sqlScriptBuilder.getLanguageDriver(), null);
    }

    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType, Method method) {
        return sqlScriptBuilder.getLanguageDriver().createSqlSource(configuration, script, parameterType);
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

    private String getResultMapId() {
        RelationalEntity relationalEntity = sqlScriptBuilder.getMapperBuildRaw().getRelationalEntity();
        String resultMapId = mapperInterface.getName() + "." + relationalEntity.getEntityClass().getName() + ".resultMap";
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
                if (property.getColumn().getJdbcType() != null) {
                    builder.jdbcType(JdbcType.forCode(property.getColumn().getJdbcType()));
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
            resultMappings.add(resultMapping);
        }

        ResultMap.Builder resultMapBuilder = new ResultMap.Builder(configuration, resultMapId
                , relationalEntity.getEntityClass().getClassType(), resultMappings);
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
        Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, mapperInterface);
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

    public SqlScriptBuilder getSqlScriptBuilder() {
        return sqlScriptBuilder;
    }
}
