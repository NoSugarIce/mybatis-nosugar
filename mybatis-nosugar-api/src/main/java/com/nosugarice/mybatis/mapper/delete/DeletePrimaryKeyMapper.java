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

import com.nosugarice.mybatis.sql.SqlBuilder;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author dingjingyang@foxmail.com
 * @date 2017/8/29
 */
public interface DeletePrimaryKeyMapper<ID> extends DeleteMapper {

    /**
     * 根据id删除
     *
     * @param id          id
     * @param logicDelete 是否软删除
     * @return
     */
    @SqlBuilder(sqlFunction = SqlBuilder.SqlFunction.DELETE_BY_ID)
    int deleteById(ID id, boolean logicDelete);

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    default int deleteById(ID id) {
        return deleteById(id, false);
    }

    /**
     * 根据id列表删除
     *
     * @param ids
     * @param logicDelete 是否软删除
     * @return
     */
    @SqlBuilder(sqlFunction = SqlBuilder.SqlFunction.DELETE_BY_IDS)
    int deleteByIds(Collection<ID> ids, boolean logicDelete);

    /**
     * 根据id列表删除
     *
     * @param ids
     * @return
     */
    default int deleteByIds(Collection<ID> ids) {
        return deleteByIds(ids, false);
    }

    /**
     * 根据id列表删除
     *
     * @param ids
     * @return
     */
    default int deleteByIds(ID... ids) {
        return deleteByIds(Arrays.asList(ids));
    }

}
