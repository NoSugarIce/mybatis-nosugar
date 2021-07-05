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

package com.nosugarice.mybatis.mapping;

import com.nosugarice.mybatis.exception.NoSugarException;
import com.nosugarice.mybatis.mapping.value.KeyValue;
import com.nosugarice.mybatis.reflection.EntityClass;
import com.nosugarice.mybatis.util.Preconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/11/22
 */
public class RelationalEntity extends Relational<RelationalEntity> {

    private final EntityClass entityClass;

    public Table table;

    /*** 名称 */
    private final String name;

    private final Set<String> propertyNames = new HashSet<>();

    private final List<RelationalProperty> properties = new ArrayList<>();

    private final Map<String, RelationalProperty> columnMap = new HashMap<>();

    private final Map<String, RelationalProperty> propertyMap = new HashMap<>();

    private final List<RelationalProperty> primaryKeyProperties = new ArrayList<>();

    private RelationalProperty idGeneratorProperty;

    private RelationalProperty versionProperty;

    private RelationalProperty logicDeleteProperty;

    public RelationalEntity(EntityClass entityClass) {
        this.entityClass = entityClass;
        this.name = entityClass.getName();
    }

    public void addProperties(Collection<RelationalProperty> relationalProperties) {
        relationalProperties.forEach(this::addProperty);
    }

    public synchronized void addProperty(RelationalProperty property) {
        property.setRelationalModel(this);
        properties.add(property);
        propertyNames.add(property.getName().toLowerCase());
        propertyMap.put(property.getName().toLowerCase(), property);
        columnMap.put(property.getColumn().getName().toLowerCase(), property);
        if (property.isPrimaryKey()) {
            primaryKeyProperties.add(property);
            if (property.getValue() instanceof KeyValue) {
                idGeneratorProperty = property;
            }
        }
        if (property.isVersion()) {
            versionProperty = property;
        }
        if (property.isLogicDelete()) {
            logicDeleteProperty = property;
        }
    }

    public RelationalProperty getOnePrimaryKeyProperty() {
        Preconditions.checkArgument(primaryKeyProperties.size() == 1, true, "未查到唯一主键字段");
        return primaryKeyProperties.stream().findFirst()
                .orElseThrow(() -> new NoSugarException("未查到主键字段"));
    }

    public RelationalProperty getPropertyByPropertyName(String propertyName) {
        return propertyMap.get(propertyName.toLowerCase());
    }

    public RelationalProperty getPropertyByColumnName(String columnName) {
        return columnMap.get(columnName.toLowerCase());
    }

    public boolean existPropertyName(String fieldName) {
        return propertyNames.contains(fieldName.toLowerCase());
    }

    public String getQualifiedName() {
        return entityClass.getClassType().getTypeName();
    }

    public EntityClass getEntityClass() {
        return entityClass;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public String getName() {
        return name;
    }

    public Set<String> getPropertyNames() {
        return propertyNames;
    }

    public List<RelationalProperty> getProperties() {
        return properties;
    }

    public Map<String, RelationalProperty> getColumnMap() {
        return columnMap;
    }

    public Map<String, RelationalProperty> getPropertyMap() {
        return propertyMap;
    }

    public List<RelationalProperty> getPrimaryKeyProperties() {
        return primaryKeyProperties;
    }

    public Optional<RelationalProperty> getIdGeneratorProperty() {
        return Optional.ofNullable(idGeneratorProperty);
    }

    public Optional<RelationalProperty> getVersionProperty() {
        return Optional.ofNullable(versionProperty);
    }

    public Optional<RelationalProperty> getLogicDeleteProperty() {
        return Optional.ofNullable(logicDeleteProperty);
    }
}
