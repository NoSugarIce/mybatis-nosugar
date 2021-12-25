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

import com.nosugarice.mybatis.config.Supports;
import com.nosugarice.mybatis.dialect.Identity;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.mapping.id.IdGenerator;
import com.nosugarice.mybatis.mapping.value.KeyValue;
import com.nosugarice.mybatis.util.Preconditions;
import com.nosugarice.mybatis.util.StringUtils;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/2
 */
public class InsertStatementBuilder extends StatementBuilder {

    InsertStatementBuilder() {
    }

    @Override
    public SqlCommandType getSqlCommandType(Method method) {
        return SqlCommandType.INSERT;
    }

    @Override
    public KeyGenerator getKeyGenerator(String mappedName) {
        String id = mappedName + SelectKeyGenerator.SELECT_KEY_SUFFIX;
        if (configuration.hasKeyGenerator(id)) {
            return configuration.getKeyGenerator(id);
        }
        KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
        Supports supports = entityMetadata.getSupports();
        if (supports.isSupportAutoIncrement()) {
            Identity identity = buildingContext.getDialect().getIdentity();
            boolean autoIncrement = identity.supportsAutoIncrement();
            if (autoIncrement) {
                keyGenerator = Jdbc3KeyGenerator.INSTANCE;
            } else {
                RelationalProperty idGeneratorProperty = entityMetadata.getIdGeneratorProperty();
                KeyValue keyValue = idGeneratorProperty.getAsKeyValue();
                String sql = keyValue.getGenerator();
                if (identity.supportsSelectIdentity()) {
                    if (StringUtils.isEmpty(sql)) {
                        sql = identity.getIdentitySelectString();
                    }
                } else {
                    Preconditions.checkArgument(StringUtils.isNotEmpty(sql)
                            , entityMetadata.getEntityClass().getName()
                                    + "." + idGeneratorProperty.getName() + "没有设置主键生成语句");
                }
                MappedStatement keyStatement = getKeyMappedStatement(id, idGeneratorProperty.getJavaType(), sql);
                keyGenerator = new SelectKeyGenerator(keyStatement, identity.executeBeforeIdentitySelect());
                configuration.addKeyGenerator(id, keyGenerator);
            }
        } else if (supports.isSupportIdGenerator()) {
            RelationalProperty relationalProperty = entityMetadata.getIdGeneratorProperty();
            MappedStatement keyStatement = getKeyMappedStatement(id, relationalProperty.getJavaType(), null);
            KeyValue keyValue = relationalProperty.getAsKeyValue();
            IdGenerator<?> idGenerator = buildingContext.getIdGeneratorRegistry().getObject(keyValue.getGenerator());
            keyGenerator = new CustomizeKeyGenerator(keyStatement, idGenerator);
            configuration.addKeyGenerator(id, keyGenerator);
        }
        return keyGenerator;
    }

    @Override
    public String getKeyProperty() {
        return entityMetadata.getRelationalEntity().getIdGeneratorProperty()
                .map(RelationalProperty::getName)
                .orElse(null);
    }

    @Override
    public String getKeyColumn() {
        return entityMetadata.getRelationalEntity().getIdGeneratorProperty()
                .map(RelationalProperty::getColumn)
                .orElse(null);
    }

    private MappedStatement getKeyMappedStatement(String id, Class<?> propertyType, String sql) {
        ResultMap.Builder resultMapBuilder = new ResultMap.Builder(configuration, id + "-Inline", propertyType, new ArrayList<>());
        List<ResultMap> resultMaps = new ArrayList<>();
        resultMaps.add(resultMapBuilder.build());

        SqlSource sqlSource = new StaticSqlSource(configuration, sql);
        MappedStatement mappedStatement = new MappedStatement.Builder(configuration, id, sqlSource, SqlCommandType.SELECT)
                .statementType(StatementType.STATEMENT)
                .keyGenerator(NoKeyGenerator.INSTANCE)
                .keyProperty(getKeyProperty())
                .keyColumn(getKeyColumn())
                .resultMaps(resultMaps)
                .build();

        configuration.addMappedStatement(mappedStatement);
        return configuration.getMappedStatement(id, false);
    }

    private static class CustomizeKeyGenerator implements KeyGenerator {

        private final MappedStatement keyStatement;
        private final IdGenerator<?> idGenerator;

        private CustomizeKeyGenerator(MappedStatement keyStatement, IdGenerator<?> idGenerator) {
            this.keyStatement = keyStatement;
            this.idGenerator = idGenerator;
        }

        @Override
        public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
            processGeneratedKeys(executor, ms, parameter);
        }

        @Override
        public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        }

        private void processGeneratedKeys(Executor executor, MappedStatement ms, Object parameter) {
            try {
                if (parameter != null && keyStatement != null && keyStatement.getKeyProperties() != null) {
                    String[] keyProperties = keyStatement.getKeyProperties();
                    final Configuration configuration = ms.getConfiguration();

                    if (parameter instanceof Map) {
                        Collection<?> collection = (Collection<?>) ((Map<?, ?>) parameter).get("collection");
                        for (Object parameterItem : collection) {
                            processGeneratedKeys(executor, ms, parameterItem);
                        }
                    } else {
                        final MetaObject metaParam = configuration.newMetaObject(parameter);
                        Object value = idGenerator.generate(executor, parameter);
                        if (value == null) {
                            throw new ExecutorException("SelectKey returned no data.");
                        } else {
                            MetaObject metaResult = configuration.newMetaObject(value);
                            if (keyProperties.length == 1) {
                                if (metaResult.hasGetter(keyProperties[0])) {
                                    setValue(metaParam, keyProperties[0], metaResult.getValue(keyProperties[0]));
                                } else {
                                    // no getter for the property - maybe just a single value object
                                    // so try that
                                    setValue(metaParam, keyProperties[0], value);
                                }
                            } else {
                                handleMultipleProperties(keyProperties, metaParam, metaResult);
                            }
                        }
                    }

                }
            } catch (ExecutorException e) {
                throw e;
            } catch (Exception e) {
                throw new ExecutorException("Error selecting key or setting result to parameter object. Cause: " + e, e);
            }
        }

        private void handleMultipleProperties(String[] keyProperties,
                                              MetaObject metaParam, MetaObject metaResult) {
            String[] keyColumns = keyStatement.getKeyColumns();

            if (keyColumns == null || keyColumns.length == 0) {
                // no key columns specified, just use the property names
                for (String keyProperty : keyProperties) {
                    setValue(metaParam, keyProperty, metaResult.getValue(keyProperty));
                }
            } else {
                if (keyColumns.length != keyProperties.length) {
                    throw new ExecutorException("If SelectKey has key columns, the number must match the number of key properties.");
                }
                for (int i = 0; i < keyProperties.length; i++) {
                    setValue(metaParam, keyProperties[i], metaResult.getValue(keyColumns[i]));
                }
            }
        }

        private void setValue(MetaObject metaParam, String property, Object value) {
            if (metaParam.hasSetter(property)) {
                metaParam.setValue(property, value);
            } else {
                throw new ExecutorException("No setter found for the keyProperty '" + property + "' in " + metaParam.getOriginalObject().getClass().getName() + ".");
            }
        }
    }

}
