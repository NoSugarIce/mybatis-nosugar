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

import com.nosugarice.mybatis.mapper.function.PrimaryKeyMapper;
import com.nosugarice.mybatis.sql.SqlBuilder;

import java.util.Collection;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/9
 */
public interface LogicDeleteByPrimaryKeyMapper<ID> extends PrimaryKeyMapper, LogicDeleteMapper {

    /**
     * 根据id逻辑删除
     *
     * @param id
     * @return
     */
    @SqlBuilder(sqlFunction = SqlBuilder.SqlFunction.LOGIC_DELETE_BY_ID, fixedParameter = true)
    int logicDeleteById(ID id);

    /**
     * 根据id列表逻辑删除
     *
     * @param ids
     * @return
     */
    @SqlBuilder(sqlFunction = SqlBuilder.SqlFunction.LOGIC_DELETE_BY_IDS)
    int logicDeleteByIds(Collection<ID> ids);

}
