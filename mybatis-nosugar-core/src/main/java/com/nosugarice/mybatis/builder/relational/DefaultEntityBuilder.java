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

package com.nosugarice.mybatis.builder.relational;

import com.nosugarice.mybatis.annotation.ColumnOptions;
import com.nosugarice.mybatis.annotation.LogicDelete;
import com.nosugarice.mybatis.data.ReservedWords;
import com.nosugarice.mybatis.exception.NoSugarException;
import com.nosugarice.mybatis.mapping.Column;
import com.nosugarice.mybatis.mapping.RelationalEntity;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.mapping.Table;
import com.nosugarice.mybatis.mapping.value.KeyValue;
import com.nosugarice.mybatis.mapping.value.LogicDeleteValue;
import com.nosugarice.mybatis.mapping.value.SimpleValue;
import com.nosugarice.mybatis.mapping.value.Value;
import com.nosugarice.mybatis.mapping.value.VersionValue;
import com.nosugarice.mybatis.reflection.EntityProperty;
import com.nosugarice.mybatis.support.NameStrategy;
import com.nosugarice.mybatis.util.Preconditions;
import com.nosugarice.mybatis.util.StringUtils;
import com.nosugarice.mybatis.valuegenerator.AnnotationParameter;
import com.nosugarice.mybatis.valuegenerator.LogicDeleteValueGenerator;

import javax.persistence.GenerationType;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/11/22
 */
public class DefaultEntityBuilder extends AbstractEntityBuilder<DefaultEntityBuilder> {

    @Override
    public RelationalEntity build() {
        javax.persistence.AccessType accessType = config.getAccessType();
        if (entityClass.isAnnotationPresent(javax.persistence.Access.class)) {
            javax.persistence.Access access = entityClass.getAnnotation(javax.persistence.Access.class);
            accessType = access.value();
        }

        RelationalEntity relationalEntity = new RelationalEntity(entityClass);

        List<EntityProperty> entityProperties = entityClass.getDeclaredProperties(accessType);
        List<RelationalProperty> relationalProperties = entityProperties.stream()
                .map(entityProperty -> new PropertyBuilder(entityProperty).build())
                .peek(relationalColumn -> relationalColumn.setRelationalModel(relationalEntity))
                .collect(Collectors.toList());

        long pkColumnCount = relationalProperties.stream().filter(RelationalProperty::isPrimaryKey).count();

        AtomicInteger pkColumnIndex = new AtomicInteger();
        AtomicInteger columnIndex = new AtomicInteger((int) pkColumnCount);

        List<Column> columns = relationalProperties.stream()
                .map(relationalColumn -> {
                    Column column = relationalColumn.getColumn();
                    if (column.isPrimaryKey()) {
                        column.setOrder(pkColumnIndex.getAndIncrement());
                    } else {
                        column.setOrder(columnIndex.getAndIncrement());
                    }
                    return column;
                })
                .collect(Collectors.toList());
        Table table = bindTable();
        table.addColumns(columns);
        relationalEntity.setTable(table);

        relationalEntity.addProperties(relationalProperties);

        checkModel(relationalEntity);

        return relationalEntity;
    }

    private Table bindTable() {
        Table table = new Table();
        if (entityClass.isAnnotationPresent(javax.persistence.Table.class)) {
            javax.persistence.Table tableAnn = entityClass.getAnnotation(javax.persistence.Table.class);
            table.setName(tableAnn.name());
            table.setSchema(tableAnn.schema());
            table.setCatalog(tableAnn.catalog());
        }
        if (StringUtils.isEmpty(table.getName())) {
            String tableName = config.getClassNameToTableNameStrategy().conversion(entityClass.getName());
            table.setName(tableName);
        }
        return table;
    }

    /**
     * 校验
     *
     * @param relationalEntity 关系模型
     */
    private static void checkModel(RelationalEntity relationalEntity) {
        int primaryKeyColumnCount = relationalEntity.getPrimaryKeyProperties().size();

        Preconditions.checkArgument(primaryKeyColumnCount > 0, false
                , "[" + relationalEntity.getTable().getName() + "] " + "最好设置一个主键,否则影响和主键相关的功能.");

        if (primaryKeyColumnCount > 1) {
            long generatedPrimaryKeyColumnCount = relationalEntity.getPrimaryKeyProperties().stream()
                    .filter(relationalProperty -> relationalProperty.getAsKeyValue().isAutoIncrement())
                    .count();
            Preconditions.checkArgument(generatedPrimaryKeyColumnCount <= 1, false
                    , "[" + relationalEntity.getTable().getName() + "] " + "自增主键只能有一个!");
        }

        Preconditions.checkArgument(relationalEntity.getProperties().stream()
                        .noneMatch(relationalColumn -> relationalColumn.getColumn().isKeyword()), false
                , "[" + relationalEntity.getTable().getName() + "] " + "存在潜在的数据库关键字,建议更改.");
    }

    private class PropertyBuilder {

        private final EntityProperty property;

        public PropertyBuilder(EntityProperty property) {
            this.property = property;
        }

        public RelationalProperty build() {
            RelationalProperty relationalProperty = new RelationalProperty();
            relationalProperty.setName(property.getName());
            relationalProperty.setJavaType(property.getClassType());
            if (config.isJavaxValidationMappingNotNull()) {
                if (property.isAnnotationPresent(javax.validation.constraints.NotNull.class)) {
                    relationalProperty.setNullable(false);
                }
            }
            if (property.isAnnotationPresent(javax.persistence.Id.class)) {
                relationalProperty.setPrimaryKey(true);
            }
            if (property.isAnnotationPresent(javax.persistence.GeneratedValue.class)) {
                Preconditions.checkArgument(relationalProperty.isPrimaryKey(), true
                        , "[" + property.getFullName() + "]" + "@GeneratedValue 设置错误!");
                javax.persistence.GeneratedValue generatedValue = property.getAnnotation(javax.persistence.GeneratedValue.class);
                Preconditions.checkArgument(generatedValue.strategy() == GenerationType.AUTO
                                || generatedValue.strategy() == GenerationType.IDENTITY
                        , true, "主键生成仅支持[AUTO],[IDENTITY]");

                KeyValue<Serializable> value = new KeyValue<>();
                value.setGenerator(generatedValue.generator());
                if (generatedValue.strategy() == GenerationType.AUTO) {
                    Preconditions.checkArgument(StringUtils.isNotEmpty(generatedValue.generator()), true
                            , "当主键策略为[AUTO]时[generator]必须指定策略标识");
                }

                if (generatedValue.strategy() == GenerationType.IDENTITY) {
                    value.setAutoIncrement(true);
                }
                relationalProperty.setValue(value);
            }
            if (property.isAnnotationPresent(javax.persistence.Version.class)) {
                Preconditions.checkArgument(!relationalProperty.isPrimaryKey() && !relationalProperty.isLogicDelete()
                        , true, "[" + property.getFullName() + "]" + "@Version 设置错误!");

                relationalProperty.setVersion(true);
                relationalProperty.setNullable(false);
                Value<Serializable> value = new VersionValue(property.getClassType());
                relationalProperty.setValue(value);
            }
            if (property.isAnnotationPresent(LogicDelete.class)) {
                Preconditions.checkArgument(!relationalProperty.isPrimaryKey() && !relationalProperty.isVersion()
                        , true, "[" + property.getFullName() + "]" + "@LogicDelete 设置错误!");
                LogicDelete logicDelete = property.getAnnotation(LogicDelete.class);
                Preconditions.checkArgument(StringUtils.isNotBlank(logicDelete.deleteValue()), true
                        , "[" + property.getFullName() + "]" + "逻辑删除值未设置!");
                relationalProperty.setLogicDelete(true);
                relationalProperty.setNullable(false);

                AnnotationParameter defaultValueParameter = new AnnotationParameter(property.getClassType(), logicDelete.defaultValue());
                AnnotationParameter deleteValueParameter = new AnnotationParameter(property.getClassType(), logicDelete.deleteValue());
                try {
                    LogicDeleteValueGenerator valueGenerator = LogicDeleteValueGenerator.getInstance();
                    Serializable defaultValue = valueGenerator.generateValue(defaultValueParameter);
                    Serializable deleteValue = valueGenerator.generateValue(deleteValueParameter);

                    Value<Serializable> value = new LogicDeleteValue(defaultValue, deleteValue);
                    relationalProperty.setValue(value);
                } catch (Exception e) {
                    throw new NoSugarException("[" + property.getFullName() + "] 逻辑删除设置格式不正确", e);
                }
            }
            if (property.isAnnotationPresent(javax.persistence.OrderBy.class)) {
                javax.persistence.OrderBy orderByAnn = property.getAnnotation(javax.persistence.OrderBy.class);
                relationalProperty.setOrderBy("ASC");
                if (StringUtils.isNotBlank(orderByAnn.value())) {
                    relationalProperty.setOrderBy(orderByAnn.value());
                }
            }
            if (property.isAnnotationPresent(ColumnOptions.class)) {
                ColumnOptions columnOptions = property.getAnnotation(ColumnOptions.class);
                relationalProperty.setIgnoreEmptyChar(columnOptions.ignoreEmptyChar());
                relationalProperty.setTypeHandler(columnOptions.typeHandler());
            }
            if (relationalProperty.getValue() == null) {
                relationalProperty.setValue(new SimpleValue<>());
            }
            relationalProperty.setColumn(buildColumn(relationalProperty));
            return relationalProperty;
        }

        private Column buildColumn(RelationalProperty relationalProperty) {
            Column column = new Column();
            String name = null;
            if (property.isAnnotationPresent(javax.persistence.Column.class)) {
                javax.persistence.Column columnAnn = property.getAnnotation(javax.persistence.Column.class);
                name = StringUtils.isNotEmpty(columnAnn.name()) ? columnAnn.name() : "";
            }
            if (StringUtils.isEmpty(name)) {
                NameStrategy fieldNameToColumnNameStrategy = config.getFieldNameToColumnNameStrategy();
                fieldNameToColumnNameStrategy.setEntityName(entityClass.getName());
                name = fieldNameToColumnNameStrategy.conversion(property.getName());
            }
            column.setName(name);
            if (property.isAnnotationPresent(javax.persistence.Column.class)) {
                javax.persistence.Column columnAnn = property.getAnnotation(javax.persistence.Column.class);
                column.setLength(columnAnn.length());
                column.setPrecision(columnAnn.precision());
                column.setScale(columnAnn.scale());
                column.setNullable(columnAnn.nullable());
            }
            column.setNullable(relationalProperty.isNullable());
            column.setPrimaryKey(relationalProperty.isPrimaryKey());
            column.setAutoIncrement(relationalProperty.isPrimaryKey() && relationalProperty.getAsKeyValue().isAutoIncrement());
            column.setDefaultValue(relationalProperty.getValue() != null ? relationalProperty.getValue().getDefaultValue() : null);
            column.setKeyword(ReservedWords.SQL.isKeyword(column.getName()));
            return column;
        }
    }

}
