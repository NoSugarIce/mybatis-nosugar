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
    public <T> CriteriaQuery<T, String> entityToSimpleCriteriaQuery(T entity) {
        return CriteriaBuilder.propertyQuery(entity).simple();
    }

    @Override
    public <T> CriteriaUpdate<T, String> entityToCriteriaUpdate(T entity) {
        return CriteriaBuilder.propertyUpdate(entity);
    }

    @Override
    public <T> CriteriaDelete<T, String> entityToCriteriaDelete(T entity) {
        return CriteriaBuilder.propertyDelete(entity);
    }

    @Override
    public <T, C> CriteriaUpdate<T, C> mergeCriteriaUpdate(T entity, CriteriaUpdate<T, C> criteria, boolean nullable) {
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
