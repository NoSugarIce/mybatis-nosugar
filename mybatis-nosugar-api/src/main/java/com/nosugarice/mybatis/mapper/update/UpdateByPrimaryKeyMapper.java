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

import com.nosugarice.mybatis.mapper.MapperParam;
import com.nosugarice.mybatis.mapper.function.PrimaryKeyMapper;
import com.nosugarice.mybatis.sql.SqlBuilder;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * @author dingjingyang@foxmail.com
 * @date 2017/8/29
 */
public interface UpdateByPrimaryKeyMapper<T> extends PrimaryKeyMapper, UpdateMapper {

    /**
     * 根据主键更新
     *
     * @param entity
     * @return
     */
    @SqlBuilder(sqlFunction = SqlBuilder.SqlFunction.UPDATE_BY_ID)
    int updateById(@Param(MapperParam.UPDATE_COLUMN) T entity);

    /**
     * 根据主键更新,选择的属性强制更新
     *
     * @param entity
     * @param choseProperties
     * @return
     */
    @SqlBuilder(sqlFunction = SqlBuilder.SqlFunction.UPDATE_BY_ID_CHOSE_PROPERTY)
    int updateByIdChoseProperty(@Param(MapperParam.UPDATE_COLUMN) T entity, @Param("choseProperties") Set<String> choseProperties);

    /**
     * 根据主键更新,值为空的忽略
     *
     * @param entity
     * @return
     */
    @SqlBuilder(sqlFunction = SqlBuilder.SqlFunction.UPDATE_NULLABLE_BY_ID)
    int updateByIdNullable(@Param(MapperParam.UPDATE_COLUMN) T entity);

}
