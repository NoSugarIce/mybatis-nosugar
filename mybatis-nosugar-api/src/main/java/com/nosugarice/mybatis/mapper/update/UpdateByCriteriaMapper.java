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

import com.nosugarice.mybatis.mapper.MapperParam;
import com.nosugarice.mybatis.mapper.function.CriteriaMapper;
import com.nosugarice.mybatis.query.criteria.EntityCriteriaQuery;
import com.nosugarice.mybatis.sql.SqlBuilder;
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
    @SqlBuilder(sqlFunction = SqlBuilder.SqlFunction.UPDATE)
    int update(@Param(MapperParam.UPDATE_COLUMN) T entity, @Param(MapperParam.CRITERIA) EntityCriteriaQuery<T> criteria);

    /**
     * 根据所选条件更新
     * null 会被忽略,不会被更新到数据库
     *
     * @param entity
     * @param criteria
     * @return
     */
    @SqlBuilder(sqlFunction = SqlBuilder.SqlFunction.UPDATE_NULLABLE)
    int updateNullable(@Param(MapperParam.UPDATE_COLUMN) T entity, @Param(MapperParam.CRITERIA) EntityCriteriaQuery<T> criteria);

}
