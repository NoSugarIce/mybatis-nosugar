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

package com.nosugarice.mybatis.mapper.update;

import com.nosugarice.mybatis.criteria.CriteriaUpdate;
import com.nosugarice.mybatis.criteria.EntityToCriterion;
import com.nosugarice.mybatis.mapper.MapperParam;
import com.nosugarice.mybatis.sql.SqlBuilder;
import org.apache.ibatis.annotations.Param;

/**
 * @author dingjingyang@foxmail.com
 * @date 2017/8/29
 */
public interface UpdateCriteriaMapper<T> extends UpdateMapper {

    /**
     * 根据所设置的值和所选条件更新
     *
     * @param criteria 查询条件
     * @return 更新行数
     */
    @SqlBuilder(sqlFunction = SqlBuilder.SqlFunction.UPDATE)
    <C> int update(@Param(MapperParam.CRITERIA) CriteriaUpdate<T, C, ?> criteria);

    /**
     * 根据所选条件更新,仅生效条件
     * null 也是值,也会被更新到数据库
     *
     * @param entity   需要更新的字段
     * @param criteria 查询条件,#set(k, v)将忽略
     * @return 更新行数
     */
    default <C> int update(T entity, CriteriaUpdate<T, C, ?> criteria) {
        return update(EntityToCriterion.getInstance().mergeCriteriaUpdate(entity, criteria, true));
    }

    /**
     * 根据所选条件更新
     * null 会被忽略,不会被更新到数据库
     *
     * @param entity   需要更新的字段
     * @param criteria ,#set(k, v)将忽略
     * @return 更新行数
     */
    default <C> int updateNullable(T entity, CriteriaUpdate<T, C, ?> criteria) {
        return update(EntityToCriterion.getInstance().mergeCriteriaUpdate(entity, criteria, false));
    }

    /**
     * 根据所选条件更新
     *
     * @param entity          需要更新的字段
     * @param selectParameter 查询条件
     * @return 更新行数
     */
    default int update(T entity, T selectParameter) {
        return update(entity, EntityToCriterion.getInstance().entityToCriteriaUpdate(selectParameter));
    }

    /**
     * 根据所选条件更新
     *
     * @param entity          需要更新的字段
     * @param selectParameter 查询条件
     * @return 更新行数
     */
    default int updateNullable(T entity, T selectParameter) {
        return updateNullable(entity, EntityToCriterion.getInstance().entityToCriteriaUpdate(selectParameter));
    }

}
