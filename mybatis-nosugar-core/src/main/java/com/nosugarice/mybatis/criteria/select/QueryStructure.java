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

package com.nosugarice.mybatis.criteria.select;

import com.nosugarice.mybatis.criteria.where.WhereStructure;
import com.nosugarice.mybatis.sql.ParameterBind;
import com.nosugarice.mybatis.sql.render.EntitySQLRender;
import com.nosugarice.mybatis.sql.render.QuerySQLRender;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Optional;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public interface QueryStructure<T> extends WhereStructure {

    /**
     * 是否简单查询
     *
     * @return
     */
    boolean isSimple();

    /**
     * 排重
     *
     * @return
     */
    boolean isDistinct();

    /**
     * 获取结果集
     *
     * @return
     */
    Optional<List<ColumnSelection>> getColumnSelections();

    /**
     * 获取count字段
     *
     * @return
     */
    Optional<String> getCountColumn();

    /**
     * 获取结果集
     *
     * @return
     */
    Optional<List<FunctionSelection>> getFunctionSelections();

    /**
     * 获取分组条件
     *
     * @return
     */
    Optional<GroupByCriterion> getGroupBy();

    /**
     * 获取Having条件
     *
     * @return
     */
    Optional<HavingCriterion> getHaving();

    /**
     * 获取排序条件
     *
     * @return
     */
    Optional<OrderByCriterion> getOrderBy();

    /**
     * 获取分页条件
     *
     * @return
     */
    Optional<RowBounds> getLimit();

    boolean isForUpdate();

    /**
     * 获取关联表条件
     *
     * @return
     */
    Optional<JoinCriteria<T>> getJoinCriteria();

    /**
     * 获取SQL渲染器
     *
     * @param sqlRender
     * @return
     */
    QuerySQLRender getRender(EntitySQLRender sqlRender);

    void setParameterBind(ParameterBind parameterBind);

    ParameterBind getParameterBind();

}
