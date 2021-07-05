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

package com.nosugarice.mybatis.mapper.insert;

import com.nosugarice.mybatis.mapper.function.Mapper;
import com.nosugarice.mybatis.sql.SqlBuilder;

import java.util.Collection;

/**
 * @author dingjingyang@foxmail.com
 * @date 2017/8/29
 */
public interface InsertMapper<T> extends Mapper {

    /**
     * 插入数据
     *
     * @param entity
     * @return
     */
    @SqlBuilder(sqlSourceFunction = SqlBuilder.SqlSourceFunction.INSERT)
    int insert(T entity);

    /**
     * 插入数据,值为NULL忽略
     *
     * @param entity
     * @return
     */
    @SqlBuilder(sqlSourceFunction = SqlBuilder.SqlSourceFunction.INSERT_NULLABLE)
    int insertNullable(T entity);

    /**
     * 批量插入数据
     *
     * @param entities
     * @return
     */
    @SqlBuilder(sqlSourceFunction = SqlBuilder.SqlSourceFunction.INSERT_BATCH)
    int insertBatch(Collection<T> entities);

}
