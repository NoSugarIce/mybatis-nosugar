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

package com.nosugarice.mybatis.criteria;

import com.nosugarice.mybatis.config.EntityMetadata;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.registry.EntityMetadataRegistry;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/12/19
 */
public class EntityToCriterionImpl implements EntityToCriterion {

    @Override
    public <T> CriteriaQuery<T, String, ?> entityToSimpleCriteriaQuery(T entity) {
        return CriteriaBuilder.propertyQuery(entity).simple();
    }

    @Override
    public <T> CriteriaUpdate<T, String, ?> entityToCriteriaUpdate(T entity) {
        return CriteriaBuilder.propertyUpdate(entity);
    }

    @Override
    public <T> CriteriaDelete<T, String, ?> entityToCriteriaDelete(T entity) {
        return CriteriaBuilder.propertyDelete(entity);
    }

    @Override
    public <T, C> CriteriaUpdate<T, C, ?> mergeCriteriaUpdate(T entity, CriteriaUpdate<T, C, ?> criteria, boolean nullable) {
        criteria.cleanValues();
        Map<String, Object> setValues = entityToSetValues(entity, nullable);
        setValues.forEach(criteria::setByColumn);
        return criteria;
    }

    private <T> Map<String, Object> entityToSetValues(T entity, boolean nullable) {
        EntityMetadata entityMetadata = EntityMetadataRegistry.getInstance().getEntityMetadata(entity.getClass());
        Map<String, Object> columnValues = new LinkedHashMap<>(entityMetadata.getRelationalEntity().getProperties().size());
        for (RelationalProperty property : entityMetadata.getRelationalEntity().getProperties()) {
            Object value = property.getValue(entity);
            if (value != null || nullable) {
                columnValues.put(property.getColumn(), value);
            }
        }
        return columnValues;
    }
}
