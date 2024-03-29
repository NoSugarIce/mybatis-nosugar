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

package com.nosugarice.mybatis.mapper.select;

import com.nosugarice.mybatis.sql.SqlBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author dingjingyang@foxmail.com
 * @date 2017/8/29
 */
public interface SelectPrimaryKeyMapper<T, ID> extends SelectMapper {

    /**
     * 根据主键查询实体
     *
     * @param id
     * @return
     */
    @SqlBuilder(sqlFunction = SqlBuilder.SqlFunction.SELECT_BY_ID, fixedParameter = true)
    Optional<T> selectById(ID id);

    /**
     * 根据 id 集合查询符合条件的所有实体
     *
     * @param ids
     * @return
     */
    @SqlBuilder(sqlFunction = SqlBuilder.SqlFunction.SELECT_BY_IDS)
    List<T> selectByIds(Collection<ID> ids);

    /**
     * 根据 id 数组查询符合条件的所有实体
     *
     * @param ids 主键数组
     * @return
     */
    default List<T> selectByIds(ID... ids) {
        return selectByIds(Arrays.asList(ids));
    }

}
