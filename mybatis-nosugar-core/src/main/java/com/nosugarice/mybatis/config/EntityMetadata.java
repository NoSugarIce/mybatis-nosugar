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

package com.nosugarice.mybatis.config;

import com.nosugarice.mybatis.mapping.RelationalEntity;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.util.Preconditions;
import com.nosugarice.mybatis.util.StringFormatter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/2
 */
public class EntityMetadata {

    private final RelationalEntity relationalEntity;
    private final Supports supports;

    private final Map<String, RelationalProperty> columnMap;
    private final Map<String, RelationalProperty> propertyMap;

    public EntityMetadata(RelationalEntity relationalEntity, MapperBuilderConfig config) {
        this.relationalEntity = relationalEntity;
        this.supports = new Supports(relationalEntity, config);
        this.columnMap = relationalEntity.getProperties().stream()
                .collect(Collectors.toMap(property -> property.getColumn().toLowerCase()
                        , Function.identity(), (o, o2) -> o, () -> new LinkedHashMap<>(relationalEntity.getProperties().size())));
        this.propertyMap = relationalEntity.getProperties().stream()
                .collect(Collectors.toMap(property -> property.getName().toLowerCase(), Function.identity()));
    }

    public RelationalProperty getPrimaryKeyProperty() {
        Preconditions.checkArgument(relationalEntity.getPrimaryKeyProperties().size() == 1
                , "未查到唯一主键字段!");
        return Preconditions.checkNotNull(relationalEntity.getPrimaryKeyProperties().stream().findFirst().orElse(null)
                , "未查到主键字段.");
    }

    public RelationalProperty getIdGeneratorProperty() {
        return Preconditions.checkNotNull(relationalEntity.getIdGeneratorProperty().orElse(null)
                , "未找到生成策略主键.");
    }

    public RelationalProperty getVersionProperty() {
        return Preconditions.checkNotNull(relationalEntity.getVersionProperty().orElse(null)
                , "未找到版本字段.");
    }

    public RelationalProperty getLogicDeleteProperty() {
        return Preconditions.checkNotNull(relationalEntity.getLogicDeleteProperty().orElse(null)
                , "未找到逻辑删除字段.");
    }

    public RelationalProperty getPropertyByPropertyName(String propertyName) {
        return Preconditions.checkNotNull(propertyMap.get(propertyName.toLowerCase())
                , StringFormatter.format("{}类中找不到属性[{}].", relationalEntity.getName(), propertyName));
    }

    public RelationalProperty getPropertyByColumnName(String column) {
        return Preconditions.checkNotNull(columnMap.get(column.toLowerCase())
                , StringFormatter.format("{}类中找不到列[{}]对应的属性.", relationalEntity.getName(), column));
    }

    public List<String> getColumns() {
        return new ArrayList<>(columnMap.keySet());
    }

    public boolean existProperty(String propertyName) {
        return propertyMap.containsKey(propertyName.toLowerCase());
    }

    public RelationalEntity getRelationalEntity() {
        return relationalEntity;
    }

    public Class<?> getEntityClass() {
        return relationalEntity.getEntityClass();
    }

    public Supports getSupports() {
        return supports;
    }

}
