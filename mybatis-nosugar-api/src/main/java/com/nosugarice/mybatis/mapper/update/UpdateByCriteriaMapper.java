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

import com.nosugarice.mybatis.mapper.function.CriteriaMapper;
import com.nosugarice.mybatis.sql.SqlBuilder;
import com.nosugarice.mybatis.sql.criteria.EntityCriteriaQuery;
import org.apache.ibatis.annotations.Param;

/**
 * @author dingjingyang@foxmail.com
 * @date 2017/8/29
 */
public interface UpdateByCriteriaMapper<T> extends CriteriaMapper, UpdateMapper {

    /**
     * 根据所选条件更新
     * null 也是值,也会被更新到数据库
     *
     * @param entity   需要更新的字段
     * @param criteria 查询条件
     * @return
     */
    @SqlBuilder(sqlSourceFunction = SqlBuilder.SqlSourceFunction.UPDATE)
    int update(@Param(UPDATE_COLUMN) T entity, @Param(CRITERIA) EntityCriteriaQuery<T> criteria);

    /**
     * 根据所选条件更新
     * null 会被忽略,不会被更新到数据库
     *
     * @param entity
     * @param criteria
     * @return
     */
    @SqlBuilder(sqlSourceFunction = SqlBuilder.SqlSourceFunction.UPDATE_NULLABLE)
    int updateNullable(@Param(UPDATE_COLUMN) T entity, @Param(CRITERIA) EntityCriteriaQuery<T> criteria);

}
