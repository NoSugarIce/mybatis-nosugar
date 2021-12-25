package com.nosugarice.mybatis.registry;

import com.nosugarice.mybatis.config.EntityMetadata;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.utils.ServiceLoaderUtils;

/**
 * 实体类相关信息
 *
 * @author dingjingyang@foxmail.com
 * @date 2021/9/18
 */
public interface EntityMetadataRegistry {

    /**
     * 注册实体类信息
     *
     * @param entityClass
     * @param entityMetadata
     */
    void register(Class<?> entityClass, EntityMetadata entityMetadata);

    EntityMetadata getEntityMetadata(Class<?> entityClass);

    String getTable(Class<?> entityClass);

    RelationalProperty getPropertyByColumn(Class<?> entityClass, String column);

    String getColumnByProperty(Class<?> entityClass, String property);

    static EntityMetadataRegistry getInstance() {
        return Holder.INSTANCE;
    }

    class Holder {
        private static final EntityMetadataRegistry INSTANCE = ServiceLoaderUtils.loadSingleInstance(EntityMetadataRegistry.class);
    }

}
