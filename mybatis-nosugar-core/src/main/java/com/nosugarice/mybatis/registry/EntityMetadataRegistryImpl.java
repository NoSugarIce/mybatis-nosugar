package com.nosugarice.mybatis.registry;

import com.nosugarice.mybatis.builder.EntityMetadata;
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
