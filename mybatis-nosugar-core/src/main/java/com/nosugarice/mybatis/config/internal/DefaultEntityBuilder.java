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

package com.nosugarice.mybatis.config.internal;

import com.nosugarice.mybatis.annotation.ColumnOptions;
import com.nosugarice.mybatis.annotation.LogicDelete;
import com.nosugarice.mybatis.config.EntityBuilder;
import com.nosugarice.mybatis.handler.ValueHandler;
import com.nosugarice.mybatis.mapping.RelationalEntity;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.mapping.Table;
import com.nosugarice.mybatis.mapping.value.KeyValue;
import com.nosugarice.mybatis.mapping.value.LogicDeleteValue;
import com.nosugarice.mybatis.mapping.value.SimpleValue;
import com.nosugarice.mybatis.mapping.value.Value;
import com.nosugarice.mybatis.mapping.value.VersionValue;
import com.nosugarice.mybatis.registry.ReservedWords;
import com.nosugarice.mybatis.support.EntityPropertyNameStrategy;
import com.nosugarice.mybatis.support.NameStrategy;
import com.nosugarice.mybatis.util.Preconditions;
import com.nosugarice.mybatis.util.ReflectionUtils;
import com.nosugarice.mybatis.util.StringUtils;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/11/22
 */
public class DefaultEntityBuilder extends EntityBuilder {

    @Override
    public RelationalEntity build() {
        RelationalEntity relationalEntity = new RelationalEntity(entityClass);
        relationalEntity.setTable(bindTable());

        AccessType accessType = config.getAccessType();
        if (entityClass.isAnnotationPresent(Access.class)) {
            Access access = entityClass.getAnnotation(Access.class);
            accessType = access.value();
        }

        List<? extends Member> members = accessType == AccessType.FIELD ? ReflectionUtils.getAllField(entityClass)
                : ReflectionUtils.getAllGetMethod(entityClass);
        members.stream()
                .filter(member -> !((AnnotatedElement) member).isAnnotationPresent(Transient.class))
                .map(member -> new PropertyBuilder(member).build())
                .forEach(relationalEntity::addProperty);

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
            String tableName = config.getClassNameToTableNameStrategy().conversion(entityClass.getSimpleName());
            table.setName(tableName);
        }
        return table;
    }

    private class PropertyBuilder {

        private final Member member;

        public PropertyBuilder(Member member) {
            if (!Field.class.isAssignableFrom(member.getClass()) && !Method.class.isAssignableFrom(member.getClass())) {
                throw new IllegalArgumentException("type is not Field or Method");
            }
            this.member = member;
        }

        public RelationalProperty build() {
            RelationalProperty relationalProperty = new RelationalProperty(member);
            relationalProperty.setName(ReflectionUtils.getPropertyName(member));
            Class<?> type = ReflectionUtils.getPropertyType(member);
            relationalProperty.setJavaType(type);
            relationalProperty.setValue(Value.SIMPLE_VALUE);
            if (isAnnotationPresent(Id.class)) {
                relationalProperty.setPrimaryKey(true);
                relationalProperty.setValue(Value.SIMPLE_KEY_VALUE);
            }
            if (isAnnotationPresent(ColumnOptions.class)) {
                ColumnOptions columnOptions = getAnnotation(ColumnOptions.class);
                if (StringUtils.isChar(type)) {
                    relationalProperty.setIgnoreEmptyChar(columnOptions.ignoreEmptyChar());
                }
                if (columnOptions.typeHandler() != ColumnOptions.VoidHandler.class) {
                    relationalProperty.setTypeHandler(columnOptions.typeHandler());
                }
                ValueHandler<?> insertHandler = null;
                if (columnOptions.insertHandler() != ColumnOptions.VoidHandler.class) {
                    valueHandlerRegistry.registerType(columnOptions.insertHandler());
                    insertHandler = valueHandlerRegistry.getObject(columnOptions.insertHandler());
                }
                ValueHandler<?> updateHandler = null;
                if (columnOptions.updateHandler() != ColumnOptions.VoidHandler.class) {
                    valueHandlerRegistry.registerType(columnOptions.updateHandler());
                    updateHandler = valueHandlerRegistry.getObject(columnOptions.updateHandler());
                }
                ValueHandler<?> resultHandler = null;
                if (columnOptions.resultHandler() != ColumnOptions.VoidHandler.class) {
                    valueHandlerRegistry.registerType(columnOptions.resultHandler());
                    resultHandler = valueHandlerRegistry.getObject(columnOptions.resultHandler());
                }
                if (insertHandler != null || updateHandler != null || resultHandler != null) {
                    SimpleValue value = new SimpleValue(relationalProperty.getJavaType());
                    value.setInsertHandler(insertHandler);
                    value.setUpdateHandler(updateHandler);
                    value.setResultHandler(resultHandler);
                    relationalProperty.setValue(value);
                }
            }

            if (isAnnotationPresent(GeneratedValue.class)) {
                Preconditions.checkArgument(relationalProperty.isPrimaryKey()
                        , "[" + member.getName() + "]" + "@GeneratedValue ????????????!");
                GeneratedValue generatedValue = getAnnotation(GeneratedValue.class);
                Preconditions.checkArgument(generatedValue.strategy() == GenerationType.AUTO
                                || generatedValue.strategy() == GenerationType.IDENTITY
                        , "?????????????????????[AUTO],[IDENTITY]");
                KeyValue value = new KeyValue();
                value.setGenerator(generatedValue.generator());
                if (generatedValue.strategy() == GenerationType.AUTO) {
                    Preconditions.checkArgument(StringUtils.isNotEmpty(generatedValue.generator())
                            , "??????????????????[AUTO]???[generator]????????????????????????");
                }
                if (generatedValue.strategy() == GenerationType.IDENTITY) {
                    value.setAutoIncrement(true);
                }
                relationalProperty.setValue(value);
            }
            if (isAnnotationPresent(Version.class)) {
                Preconditions.checkArgument(!relationalProperty.isPrimaryKey() && !relationalProperty.isLogicDelete()
                        , "[" + member.getName() + "]" + "@Version ????????????!");
                relationalProperty.setVersion(true);
                relationalProperty.setNullable(false);
                Value value = VersionValue.of(type);
                relationalProperty.setValue(value);
            }
            if (isAnnotationPresent(LogicDelete.class)) {
                Preconditions.checkArgument(!relationalProperty.isPrimaryKey() && !relationalProperty.isVersion()
                        , "[" + member.getName() + "]" + "@LogicDelete ????????????!");
                LogicDelete logicDelete = getAnnotation(LogicDelete.class);
                Preconditions.checkArgument(StringUtils.isNotBlank(logicDelete.deleteValue())
                        , "[" + member.getName() + "]" + "????????????????????????!");
                relationalProperty.setLogicDelete(true);
                Value value = new LogicDeleteValue(type, logicDelete.defaultValue(), logicDelete.deleteValue());
                relationalProperty.setValue(value);
            }
            if (isAnnotationPresent(OrderBy.class)) {
                OrderBy orderByAnn = getAnnotation(OrderBy.class);
                relationalProperty.setOrderBy("ASC");
                if (StringUtils.isNotBlank(orderByAnn.value())) {
                    relationalProperty.setOrderBy(orderByAnn.value());
                }
            }
            String column = null;
            if (isAnnotationPresent(Column.class)) {
                Column columnAnn = getAnnotation(Column.class);
                column = StringUtils.isNotEmpty(columnAnn.name()) ? columnAnn.name() : "";
                relationalProperty.setLength(columnAnn.length());
                relationalProperty.setPrecision(columnAnn.precision());
                relationalProperty.setScale(columnAnn.scale());
                relationalProperty.setNullable(columnAnn.nullable());
            }
            if (StringUtils.isEmpty(column)) {
                NameStrategy fieldNameToColumnNameStrategy = config.getFieldNameToColumnNameStrategy();
                if (fieldNameToColumnNameStrategy instanceof EntityPropertyNameStrategy) {
                    ((EntityPropertyNameStrategy) fieldNameToColumnNameStrategy).setEntityName(entityClass.getName());
                }
                column = fieldNameToColumnNameStrategy.conversion(relationalProperty.getName());
            }
            relationalProperty.setColumn(column);
            relationalProperty.setSqlKeyword(ReservedWords.SQL.isKeyword(column));
            return relationalProperty;
        }

        private <T extends Annotation> T getAnnotation(Class<T> annotationType) {
            return ((AnnotatedElement) member).getAnnotation(annotationType);
        }

        private <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationType) {
            return ((AnnotatedElement) member).isAnnotationPresent(annotationType);
        }
    }

    /**
     * ??????
     *
     * @param relationalEntity ????????????
     */
    private static void checkModel(RelationalEntity relationalEntity) {
        int primaryKeyColumnCount = relationalEntity.getPrimaryKeyProperties().size();

        Preconditions.checkArgument(primaryKeyColumnCount > 0, false
                , "[" + relationalEntity.getTable().getName() + "] " + "????????????????????????,????????????????????????????????????.");

        if (primaryKeyColumnCount > 1) {
            long generatedPrimaryKeyColumnCount = relationalEntity.getPrimaryKeyProperties().stream()
                    .filter(relationalProperty -> relationalProperty.getAsKeyValue().isAutoIncrement())
                    .count();
            Preconditions.checkArgument(generatedPrimaryKeyColumnCount <= 1, false
                    , "[" + relationalEntity.getTable().getName() + "] " + "???????????????????????????!");
        }

        Preconditions.checkArgument(relationalEntity.getProperties().stream()
                        .noneMatch(RelationalProperty::isSqlKeyword), false
                , "[" + relationalEntity.getTable().getName() + "] " + "?????????????????????????????????,????????????.");
    }

}
