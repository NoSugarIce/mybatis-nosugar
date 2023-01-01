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

package com.nosugarice.mybatis.sql;

import com.nosugarice.mybatis.criteria.CriteriaDelete;
import com.nosugarice.mybatis.criteria.CriteriaQuery;
import com.nosugarice.mybatis.criteria.CriteriaUpdate;

import java.util.Collection;
import java.util.Set;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/6
 */
public interface ProviderTempLate {

    <ID> SqlAndParameterBind selectById(ID id);

    <ID> SqlAndParameterBind selectByIds(Collection<ID> ids);

    <T> SqlAndParameterBind selectList(CriteriaQuery<T, ?, ?> criteria);

    <T> SqlAndParameterBind insert(T entity);

    <T> SqlAndParameterBind insertNullable(T entity);

    <T> SqlAndParameterBind updateById(T entity, boolean nullable);

    <T> SqlAndParameterBind updateByIdChoseProperty(T entity, Set<String> choseProperties);

    <T> SqlAndParameterBind update(CriteriaUpdate<T, ?, ?> criteria);

    <ID> SqlAndParameterBind deleteById(ID id);

    <ID> SqlAndParameterBind deleteByIds(Collection<ID> ids);

    <T> SqlAndParameterBind delete(CriteriaDelete<T, ?, ?> criteria);

    <ID> SqlAndParameterBind logicDeleteById(ID id);

    <ID> SqlAndParameterBind logicDeleteByIds(Collection<ID> ids);

    <T> SqlAndParameterBind logicDelete(CriteriaDelete<T, ?, ?> criteria);

    //--------------jpa------------------------

    SqlAndParameterBind provideJpaFind(boolean distinct, String whereSql, String orderBy, Integer limit);

    SqlAndParameterBind provideJpaCount(String whereSql);

    SqlAndParameterBind provideJpaExists(String whereSql);

    SqlAndParameterBind provideJpaDelete(String whereSql);

    SqlAndParameterBind provideJpaLogicDelete(String whereSql);
}
