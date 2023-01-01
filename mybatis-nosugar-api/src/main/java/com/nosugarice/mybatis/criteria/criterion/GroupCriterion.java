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

package com.nosugarice.mybatis.criteria.criterion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * 组查询条件
 * 组查询第一个条件会默认AND
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public interface GroupCriterion extends Criterion {

    @Override
    default <S> S accept(CriterionSQLVisitor<S> visitor) {
        return visitor.visit(this);
    }

    /**
     * 添加属性条件
     *
     * @param criterion
     * @return
     */
    GroupCriterion append(Criterion criterion);

    /**
     * 添加属性条件
     *
     * @param criterions
     * @return
     */
    default GroupCriterion append(Criterion... criterions) {
        if (criterions != null) {
            Stream.of(criterions).forEach(this::append);
        }
        return this;
    }

    /**
     * 添加一组属性条件
     *
     * @param criterions
     * @return
     */
    default GroupCriterion appendAll(Collection<? extends Criterion> criterions) {
        if (criterions != null) {
            criterions.forEach(this::append);
        }
        return this;
    }

    /**
     * 是否包含有效条件
     *
     * @return
     */
    boolean hasCriterion();

    /**
     * 获取当前组所有条件
     *
     * @return
     */
    List<Criterion> getCriterions();

    /**
     * 获取所有属性条件
     *
     * @return
     */
    default List<ColumnCriterion<?>> getColumnCriterions() {
        return getColumnCriterions(GroupCriterion.this, null);
    }

    static List<ColumnCriterion<?>> getColumnCriterions(GroupCriterion groupCriterion, List<ColumnCriterion<?>> criterionList) {
        if (criterionList == null) {
            criterionList = new ArrayList<>();
        }
        for (Criterion criterion : groupCriterion.getCriterions()) {
            if (criterion instanceof GroupCriterion) {
                criterionList = getColumnCriterions((GroupCriterion) criterion, criterionList);
            } else if (criterion instanceof ColumnCriterion) {
                criterionList.add((ColumnCriterion<?>) criterion);
            }
        }
        return criterionList;
    }

    /**
     * 过滤空组条件及不符合的属性条件
     */
    default void filterCondition() {
        filterCondition(getCriterions());
    }

    /**
     * 过滤不符合条件的条件
     *
     * @param collections
     * @return
     */
    default Collection<? extends Criterion> filterCondition(Collection<? extends Criterion> collections) {
        Iterator<? extends Criterion> iterator = collections.iterator();
        while (iterator.hasNext()) {
            Criterion criterion = iterator.next();
            if (criterion instanceof GroupCriterion) {
                filterCondition(((GroupCriterion) criterion).getCriterions());
                if (!((GroupCriterion) criterion).hasCriterion()) {
                    iterator.remove();
                }
            } else {
                if (!criterion.condition()) {
                    iterator.remove();
                }
            }
        }
        return collections;
    }

}
