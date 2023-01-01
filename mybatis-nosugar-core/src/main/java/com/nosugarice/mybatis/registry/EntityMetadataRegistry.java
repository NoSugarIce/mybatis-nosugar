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
