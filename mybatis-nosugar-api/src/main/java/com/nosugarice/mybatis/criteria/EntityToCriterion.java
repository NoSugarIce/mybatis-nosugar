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

import com.nosugarice.mybatis.utils.ServiceLoaderUtils;

/**
 * 实体转到结构体的相关操作
 *
 * @author dingjingyang@foxmail.com
 * @date 2021/12/19
 */
public interface EntityToCriterion {

    /**
     * 实体对象转简单的查询体
     *
     * @param entity
     * @param <T>
     * @return
     */
    <T> CriteriaQuery<T, String, ?> entityToSimpleCriteriaQuery(T entity);

    /**
     * 实体对象转更新查询体
     *
     * @param entity
     * @param <T>
     * @return
     */
    <T> CriteriaUpdate<T, String, ?> entityToCriteriaUpdate(T entity);

    /**
     * 实体对象转删除查询体
     *
     * @param entity
     * @param <T>
     * @return
     */
    <T> CriteriaDelete<T, String, ?> entityToCriteriaDelete(T entity);

    /**
     * 将实体属性合并到更新结构体
     *
     * @param entity
     * @param criteriaUpdate
     * @param selective
     * @param <T>
     * @return
     */
    <T, C> CriteriaUpdate<T, C, ?> mergeCriteriaUpdate(T entity, CriteriaUpdate<T, C, ?> criteriaUpdate, boolean selective);

    /**
     * 获取实例
     *
     * @return
     */
    static EntityToCriterion getInstance() {
        return Holder.INSTANCE;
    }

    class Holder {
        private static final EntityToCriterion INSTANCE = ServiceLoaderUtils.loadSingleInstance(EntityToCriterion.class);
    }

}
