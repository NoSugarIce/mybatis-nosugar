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

import com.nosugarice.mybatis.annotation.SpeedBatch;
import com.nosugarice.mybatis.mapper.function.BatchMapper;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author dingjingyang@foxmail.com
 * @date 2017/8/30
 */
public interface DefaultUpdateByPrimaryKeyMapper<T> extends UpdateByPrimaryKeyMapper<T>, BatchMapper {

    /**
     * 根据主键更新选定
     *
     * @param entity
     * @param choseKey
     * @return
     */
    default int updateByIdChoseKey(T entity, String... choseKey) {
        return updateByIdChoseProperty(entity, new HashSet<>(Arrays.asList(choseKey)));
    }

    /**
     * 使用批处理模式更新
     *
     * @param entities  实体列表
     * @param batchSize 每批的数量
     * @param nullable  字段是否忽空值
     */
    @SpeedBatch
    default void updateByIdBatchMode(Iterable<T> entities, int batchSize, boolean nullable) {
        int index = 0;
        for (T entity : entities) {
            int i = nullable ? updateByIdNullable(entity) : updateById(entity);
            index++;
            if (index % batchSize == 0) {
                flush();
            }
        }
        flush();
    }

}
