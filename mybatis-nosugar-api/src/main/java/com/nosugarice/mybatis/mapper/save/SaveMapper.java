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

package com.nosugarice.mybatis.mapper.save;

import com.nosugarice.mybatis.entity.EntityData;
import com.nosugarice.mybatis.mapper.insert.InsertMapper;
import com.nosugarice.mybatis.mapper.select.SelectPrimaryKeyMapper;
import com.nosugarice.mybatis.mapper.update.UpdatePrimaryKeyMapper;

import java.util.Optional;

/**
 * @author dingjingyang@foxmail.com
 * @date 2017/8/30
 */
public interface SaveMapper<T, ID> extends SelectPrimaryKeyMapper<T, ID>, UpdatePrimaryKeyMapper<T>, InsertMapper<T> {

    /**
     * 保存数据,当主键为空时插入,当主键有值时更新,不会忽略NULL值
     *
     * @param entity
     * @return
     */
    default T save(T entity) {
        EntityData entityData = EntityData.Holder.getInstance();
        ID id = entityData.getId(entity);
        boolean present = Optional.ofNullable(id)
                .map(this::selectById)
                .isPresent();
        int i = present ? updateById(entity) : insert(entity);
        return entity;
    }

}
