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

import com.nosugarice.mybatis.query.criterion.ColumnCriterion;
import com.nosugarice.mybatis.query.criterion.GroupCriterion;

import java.util.Collection;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public interface PredicateCondition<T, C, X extends PredicateCondition<T, C, X>> extends CriterionBuilder<T, C> {

    /**
     * 添加条件
     *
     * @param test       是否添加到条件列表
     * @param criterions
     * @return
     */
    X addCriterion(boolean test, ColumnCriterion<?>... criterions);

    /**
     * 添加组条件
     *
     * @param groupCriterions
     * @return
     */
    X addGroupCriterion(GroupCriterion... groupCriterions);

    /**
     * 添加条件
     *
     * @param criterions
     * @return
     */
    default X addCriterion(ColumnCriterion<?>... criterions) {
        return addCriterion(true, criterions);
    }


    default X isNull(C column) {
        return addCriterion(true, buildIsNull(column));
    }

    default X notNull(C column) {
        return addCriterion(true, buildNotNull(column));
    }

    default X empty(C column) {
        return addCriterion(true, buildEmpty(column));
    }

    default X notEmpty(C column) {
        return addCriterion(true, buildNotEmpty(column));
    }

    default <V> X equalTo(C column, V value) {
        return addCriterion(true, buildEqualTo(column, value));
    }

    default <V> X notEqualTo(C column, V value) {
        return addCriterion(true, buildNotEqualTo(column, value));
    }

    default <V> X greaterThan(C column, V value) {
        return addCriterion(true, buildGreaterThan(column, value));
    }

    default <V> X notGreaterThan(C column, V value) {
        return addCriterion(true, buildNotGreaterThan(column, value));
    }

    default <V> X greaterThanOrEqualTo(C column, V value) {
        return addCriterion(true, buildGreaterThan(column, value));
    }

    default <V> X notGreaterThanOrEqualTo(C column, V value) {
        return addCriterion(true, buildNotGreaterThan(column, value));
    }

    default <V> X lessThan(C column, V value) {
        return addCriterion(true, buildLessThan(column, value));
    }

    default <V> X notLessThan(C column, V value) {
        return addCriterion(true, buildNotLessThan(column, value));
    }

    default <V> X lessThanOrEqualTo(C column, V value) {
        return addCriterion(true, buildLessThanOrEqualTo(column, value));
    }

    default <V> X notLessThanOrEqualTo(C column, V value) {
        return addCriterion(true, buildNotLessThanOrEqualTo(column, value));
    }

    default <V> X between(C column, V value, V value1) {
        return addCriterion(true, buildBetween(column, value, value1));
    }

    default <V> X notBetween(C column, V value, V value1) {
        return addCriterion(true, buildNotBetween(column, value, value1));
    }

    default <V> X in(C column, V[] values) {
        return addCriterion(true, buildIn(column, values));
    }

    default <V> X in(C column, Collection<V> values) {
        return addCriterion(true, buildIn(column, values));
    }

    default <V> X notIn(C column, V[] values) {
        return addCriterion(true, buildNotIn(column, values));
    }

    default <V> X notIn(C column, Collection<V> values) {
        return addCriterion(true, buildNotIn(column, values));
    }

    default X like(C column, String value) {
        return addCriterion(true, buildLike(column, value));
    }

    default X notLike(C column, String value) {
        return addCriterion(true, buildNotLike(column, value));
    }

    default X likeBefore(C column, String value) {
        return addCriterion(true, buildLikeBefore(column, value));
    }

    default X notLikeBefore(C column, String value) {
        return addCriterion(true, buildNotLikeBefore(column, value));
    }

    default X likeAfter(C column, String value) {
        return addCriterion(true, buildLikeAfter(column, value));
    }

    default X notLikeAfter(C column, String value) {
        return addCriterion(true, buildNotLikeAfter(column, value));
    }

}
