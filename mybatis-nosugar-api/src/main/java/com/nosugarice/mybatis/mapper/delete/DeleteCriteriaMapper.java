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

package com.nosugarice.mybatis.mapper.delete;

import com.nosugarice.mybatis.criteria.CriteriaDelete;
import com.nosugarice.mybatis.criteria.EntityToCriterion;
import com.nosugarice.mybatis.mapper.MapperParam;
import com.nosugarice.mybatis.sql.SqlBuilder;
import org.apache.ibatis.annotations.Param;

/**
 * @author dingjingyang@foxmail.com
 * @date 2017/8/29
 */
public interface DeleteCriteriaMapper<T> extends DeleteMapper {

    /**
     * 根据查询的条件进行删除
     *
     * @param criteria
     * @param logicDelete
     * @return
     */
    @SqlBuilder(sqlFunction = SqlBuilder.SqlFunction.DELETE)
    <C> int delete(@Param(MapperParam.CRITERIA) CriteriaDelete<T, C> criteria, boolean logicDelete);

    /**
     * 根据查询的条件进行删除
     *
     * @param criteria
     * @param <C>
     * @return
     */
    default <C> int delete(CriteriaDelete<T, C> criteria) {
        return delete(criteria, false);
    }

    /**
     * 根据查询的条件进行删除
     *
     * @param entity
     * @return
     */
    default int delete(T entity) {
        return delete(EntityToCriterion.getInstance().entityToCriteriaDelete(entity));
    }

}
