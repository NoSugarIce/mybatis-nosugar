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

package com.nosugarice.mybatis.mapper.update;

import com.nosugarice.mybatis.annotation.SpeedBatch;
import com.nosugarice.mybatis.mapper.MapperParam;
import com.nosugarice.mybatis.mapper.function.BatchMapper;
import com.nosugarice.mybatis.sql.SqlBuilder;
import org.apache.ibatis.annotations.Param;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author dingjingyang@foxmail.com
 * @date 2017/8/29
 */
public interface UpdatePrimaryKeyMapper<T> extends BatchMapper, UpdateMapper {

    /**
     * 根据主键更新
     *
     * @param entity   实体
     * @param nullable 属性为空的时候是否忽略,true:忽略,false:不忽略
     * @return
     */
    @SqlBuilder(sqlFunction = SqlBuilder.SqlFunction.UPDATE_BY_ID)
    int updateById(@Param(MapperParam.UPDATE_COLUMN) T entity, @Param("nullable") boolean nullable);

    /**
     * 根据主键更新,属性值为空的也写入
     *
     * @param entity
     * @return
     */
    default int updateById(T entity) {
        return updateById(entity, false);
    }

    /**
     * 根据主键更新,属性值为空的忽略
     *
     * @param entity
     * @return
     */
    default int updateByIdNullable(T entity) {
        return updateById(entity, true);
    }

    /**
     * 根据主键更新,选择的属性强制更新
     *
     * @param entity
     * @param choseProperties
     * @return
     */
    @SqlBuilder(sqlFunction = SqlBuilder.SqlFunction.UPDATE_BY_ID_CHOSE_PROPERTY)
    int updateByIdChoseKey(@Param(MapperParam.UPDATE_COLUMN) T entity, @Param("choseProperties") Set<String> choseProperties);

    /**
     * 根据主键更新选定
     *
     * @param entity
     * @param choseKey
     * @return
     */
    default int updateByIdChoseKey(T entity, String... choseKey) {
        return updateByIdChoseKey(entity, new HashSet<>(Arrays.asList(choseKey)));
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
