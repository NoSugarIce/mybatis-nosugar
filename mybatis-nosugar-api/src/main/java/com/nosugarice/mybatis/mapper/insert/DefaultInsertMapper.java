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

import com.nosugarice.mybatis.annotation.SpeedBatch;
import com.nosugarice.mybatis.mapper.function.BatchMapper;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/9
 */
public interface DefaultInsertMapper<T> extends InsertMapper<T>, BatchMapper {

    /**
     * 批量模式插入
     *
     * @param entities  实体列表
     * @param batchSize 每批的数量
     * @param nullable  字段是否忽空值
     */
    @SpeedBatch
    default void insertBatch(Iterable<T> entities, int batchSize, boolean nullable) {
        int index = 0;
        for (T entity : entities) {
            int i = nullable ? insertNullable(entity) : insert(entity);
            index++;
            if (index % batchSize == 0) {
                flush();
            }
        }
        flush();
    }

    /**
     * 批量模式插入,默认每批次1000条,属性不忽略空值
     *
     * @param entities
     */
    @SpeedBatch
    default void insertBatch(Iterable<T> entities) {
        insertBatch(entities, 1000, false);
    }

}
