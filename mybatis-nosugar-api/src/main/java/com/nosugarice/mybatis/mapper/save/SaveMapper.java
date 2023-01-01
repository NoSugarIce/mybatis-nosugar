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
        return save(entity, false);
    }


    /**
     * 保存数据,当主键为空时插入,当主键有值时更新,
     * 当merge=true的时候会主动从数据库查询记录和要保存的数据进行对比,仅更新值变化的列
     *
     * @param entity 实体对象
     * @param merge  是否合并
     * @return
     */
    default T save(T entity, boolean merge) {
        EntityData entityData = EntityData.Holder.getInstance();
        ID id = entityData.getId(entity);
        T sourceEntity = Optional.ofNullable(id)
                .map(this::selectById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .orElse(null);

        int i = sourceEntity == null ? insert(entity)
                : merge ? updateByIdChoseKey(entity, entityData.diffValueProperty(sourceEntity, entity)) : updateById(entity);
        return entity;
    }


}
