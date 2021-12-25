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

package com.nosugarice.mybatis.support;

import com.nosugarice.mybatis.config.EntityMetadata;
import com.nosugarice.mybatis.config.OrderComparator;
import com.nosugarice.mybatis.entity.Entity;
import com.nosugarice.mybatis.entity.EntityData;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.registry.EntityMetadataRegistry;
import com.nosugarice.mybatis.util.Preconditions;

import java.util.Optional;

/**
 * p
 *
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public class EntityDataImpl implements EntityData, OrderComparator {

    @SuppressWarnings("unchecked")
    @Override
    public <T, ID> ID getId(T entity) {
        if (entity instanceof Entity) {
            return ((Entity) entity).getId();
        }
        RelationalProperty pkProperty = Optional.of(EntityMetadataRegistry.getInstance())
                .map(entityMetadataRegistry -> entityMetadataRegistry.getEntityMetadata(entity.getClass()))
                .map(EntityMetadata::getPrimaryKeyProperty)
                .orElse(null);

        return (ID) Preconditions.checkNotNull(pkProperty, entity.getClass().getName() + ":未找到主键属性!").getValue(entity);
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
