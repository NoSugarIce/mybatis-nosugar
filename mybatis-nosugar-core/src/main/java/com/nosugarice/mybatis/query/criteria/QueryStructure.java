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

package com.nosugarice.mybatis.query.criteria;

import com.nosugarice.mybatis.query.criteria.function.FunctionSelection;
import com.nosugarice.mybatis.query.criterion.GroupCriterion;
import com.nosugarice.mybatis.query.process.GroupByCriterion;
import com.nosugarice.mybatis.query.process.HavingCriterion;
import com.nosugarice.mybatis.query.process.SortCriterion;
import com.nosugarice.mybatis.sql.render.CriteriaQuerySQLRender;
import com.nosugarice.mybatis.sql.render.EntitySQLRender;

import java.util.List;
import java.util.Optional;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public interface QueryStructure<T> extends From<T> {

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
     * 获取结果集
     *
     * @return
     */
    Optional<List<FunctionSelection>> getFunctionSelections();

    /**
     * 获取查查询条件
     *
     * @return
     */
    Optional<List<GroupCriterion>> getGroupCriterions();

    /**
     * 存在条件
     *
     * @return
     */
    default boolean hasCriterion() {
        int sum = getGroupCriterions()
                .map(groupCriterions -> groupCriterions.stream().mapToInt(groupCriterion -> groupCriterion.getCriterions().size()).sum())
                .orElse(0);
        return sum > 0;
    }

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
    Optional<SortCriterion> getSort();

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
    CriteriaQuerySQLRender getRender(EntitySQLRender sqlRender);

}
