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

package com.nosugarice.mybatis.mapper.update;

import com.nosugarice.mybatis.sql.criteria.SimpleCriteriaQuery;

/**
 * @author dingjingyang@foxmail.com
 * @date 2017/8/30
 */
public interface DefaultUpdateByCriteriaMapper<T> extends UpdateByCriteriaMapper<T> {

    /**
     * 根据所选条件更新
     *
     * @param entity          需要更新的字段
     * @param selectParameter 查询条件
     * @return
     */
    default int update(T entity, T selectParameter) {
        return update(entity, new SimpleCriteriaQuery<>(selectParameter));
    }

    /**
     * 根据所选条件更新
     *
     * @param entity          需要更新的字段
     * @param selectParameter 查询条件
     * @return
     */
    default int updateNullable(T entity, T selectParameter) {
        return updateNullable(entity, new SimpleCriteriaQuery<>(selectParameter));
    }

}
