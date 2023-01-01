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

package com.nosugarice.mybatis.mapping;

import com.nosugarice.mybatis.assign.value.KeyValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/11/22
 */
public class RelationalEntity {

    private final Class<?> entityClass;

    private final String name;

    private Table table;

    private final List<RelationalProperty> properties = new ArrayList<>();

    private final List<RelationalProperty> primaryKeyProperties = new ArrayList<>();

    private RelationalProperty idGeneratorProperty;

    private RelationalProperty versionProperty;

    private RelationalProperty logicDeleteProperty;

    public RelationalEntity(Class<?> entityClass) {
        this.entityClass = entityClass;
        this.name = entityClass.getSimpleName();
    }

    public synchronized void addProperty(RelationalProperty property) {
        if (property.isPrimaryKey()) {
            properties.add(0, property);
        } else {
            properties.add(property);
        }

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

    public Class<?> getEntityClass() {
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

    public List<RelationalProperty> getProperties() {
        return properties;
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
