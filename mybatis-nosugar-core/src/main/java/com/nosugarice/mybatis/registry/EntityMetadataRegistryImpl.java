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

package com.nosugarice.mybatis.registry;

import com.nosugarice.mybatis.config.EntityMetadata;
import com.nosugarice.mybatis.config.OrderComparator;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.util.Preconditions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public class EntityMetadataRegistryImpl implements EntityMetadataRegistry, OrderComparator {

    private final Map<Class<?>, EntityMetadata> entityTableIndexMap = new HashMap<>();

    @Override
    public void register(Class<?> entityClass, EntityMetadata entityMetadata) {
        entityTableIndexMap.put(entityClass, entityMetadata);
    }

    @Override
    public EntityMetadata getEntityMetadata(Class<?> entityClass) {
        return Preconditions.checkNotNull(entityTableIndexMap.get(entityClass), "未找到实体类注册信息.");
    }

    @Override
    public String getTable(Class<?> entityClass) {
        return getEntityMetadata(entityClass).getRelationalEntity().getTable().getName();
    }

    @Override
    public RelationalProperty getPropertyByColumn(Class<?> entityClass, String column) {
        return Optional.ofNullable(getEntityMetadata(entityClass))
                .map(entityMetadata -> entityMetadata.getPropertyByColumnName(column))
                .orElse(null);
    }

    @Override
    public String getColumnByProperty(Class<?> entityClass, String property) {
        return Optional.ofNullable(getEntityMetadata(entityClass))
                .map(entityMetadata -> entityMetadata.getPropertyByPropertyName(property))
                .map(RelationalProperty::getColumn)
                .orElse(null);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
