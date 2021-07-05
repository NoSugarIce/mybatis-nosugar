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

package com.nosugarice.mybatis.mapper.select;

import com.nosugarice.mybatis.annotation.Provider;
import com.nosugarice.mybatis.domain.Page;
import com.nosugarice.mybatis.mapper.function.CriteriaMapper;
import com.nosugarice.mybatis.sql.SqlBuilder;
import com.nosugarice.mybatis.sql.criteria.EntityCriteriaQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dingjingyang@foxmail.com
 * @date 2017/8/29
 */
public interface SelectByCriteriaMapper<T> extends CriteriaMapper, SelectMapper {

    /**
     * 查询符合条件的记录
     *
     * @param criteria 查询条件封装
     * @return
     */
    @SqlBuilder(sqlSourceFunction = SqlBuilder.SqlSourceFunction.SELECT_LIST)
    List<T> selectList(@Param(CRITERIA) EntityCriteriaQuery<T> criteria);

    /**
     * 分页查询
     *
     * @param criteria 查询条件封装
     * @param page     分页参数
     * @return
     */
    @SqlBuilder(sqlSourceFunction = SqlBuilder.SqlSourceFunction.SELECT_LIST)
    List<T> selectListLimit(@Param(CRITERIA) EntityCriteriaQuery<T> criteria, @Param("page") Page<T> page);

    /**
     * 查询符合条件的记录数
     *
     * @param criteria 查询条件封装
     * @return
     */
    @Provider(Provider.Type.COUNT)
    long count(@Param(CRITERIA) EntityCriteriaQuery<T> criteria);

}
