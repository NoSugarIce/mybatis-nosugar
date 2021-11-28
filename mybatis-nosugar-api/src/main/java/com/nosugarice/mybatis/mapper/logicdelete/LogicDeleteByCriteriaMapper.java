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

package com.nosugarice.mybatis.mapper.logicdelete;

import com.nosugarice.mybatis.mapper.MapperParam;
import com.nosugarice.mybatis.mapper.function.CriteriaMapper;
import com.nosugarice.mybatis.query.criteria.EntityCriteriaQuery;
import com.nosugarice.mybatis.sql.SqlBuilder;
import org.apache.ibatis.annotations.Param;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/9
 */
public interface LogicDeleteByCriteriaMapper<T> extends CriteriaMapper, LogicDeleteMapper {

    /**
     * 根据查询条件删除
     *
     * @param criteria
     * @return
     */
    @SqlBuilder(sqlFunction = SqlBuilder.SqlFunction.LOGIC_DELETE)
    int logicDelete(@Param(MapperParam.CRITERIA) EntityCriteriaQuery<T> criteria);

}
