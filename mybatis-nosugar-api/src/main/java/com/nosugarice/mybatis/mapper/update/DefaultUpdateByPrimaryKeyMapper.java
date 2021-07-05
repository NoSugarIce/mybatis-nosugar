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
import com.nosugarice.mybatis.mapper.function.MapperHelp;
import com.nosugarice.mybatis.mapper.function.MapperService;
import com.nosugarice.mybatis.mapper.insert.InsertMapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author dingjingyang@foxmail.com
 * @date 2017/8/30
 */
public interface DefaultUpdateByPrimaryKeyMapper<T> extends UpdateByPrimaryKeyMapper<T>, InsertMapper<T>, BatchMapper {

    default int updateByIdChoseKey(T entity, String... choseKey) {
        return updateByIdChoseKey(entity, new HashSet<>(Arrays.asList(choseKey)));
    }

    /**
     * 保存数据,当主键为空时插入,当主键有值时更新,不会忽略NULL值
     *
     * @param entity
     * @return
     */
    default int save(T entity) {
        MapperService mapperService = MapperHelp.getMapperService();
        boolean pkColumnValueNotNull = mapperService.entityIsNew(entity);
        return pkColumnValueNotNull ? updateById(entity) : insert(entity);
    }

    /**
     * 使用批处理模式更新
     *
     * @param list
     */
    @SpeedBatch
    default void updateByIdBatchMode(Collection<T> list) {
        list.forEach(this::updateById);
        flush();
    }

    /**
     * 使用批处理模式更新
     * 属性可以为Null
     *
     * @param list
     */
    @SpeedBatch
    default void updateNullableByIdBatchMode(Collection<T> list) {
        list.forEach(this::updateByIdNullable);
        flush();
    }

}
