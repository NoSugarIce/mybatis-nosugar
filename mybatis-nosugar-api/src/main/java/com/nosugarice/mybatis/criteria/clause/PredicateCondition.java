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

package com.nosugarice.mybatis.criteria.clause;

import com.nosugarice.mybatis.criteria.Query;
import com.nosugarice.mybatis.criteria.criterion.Criterion;
import com.nosugarice.mybatis.criteria.criterion.CriterionBuilder;

import java.util.Collection;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public interface PredicateCondition<C, X extends PredicateCondition<C, X>> extends CriterionBuilder<C> {

    /**
     * 添加条件
     *
     * @param test       是否添加到条件列表
     * @param criterions
     * @return
     */
    X addCriterion(boolean test, Criterion... criterions);

    /**
     * 添加条件
     *
     * @param criterions
     * @return
     */
    default X addCriterion(Criterion... criterions) {
        return addCriterion(true, criterions);
    }

    default X isNull(C column) {
        return addCriterion(true, buildIsNull(column));
    }

    default X isNotNull(C column) {
        return addCriterion(true, buildIsNull(column).not());
    }

    default X isEmpty(C column) {
        return addCriterion(true, buildIsEmpty(column));
    }

    default X isNotEmpty(C column) {
        return addCriterion(true, buildIsEmpty(column).not());
    }

    default <V> X equalTo(C column, V value) {
        return addCriterion(true, buildEqualTo(column, value));
    }

    default <V> X notEqualTo(C column, V value) {
        return addCriterion(true, buildEqualTo(column, value).not());
    }

    default <V> X greaterThan(C column, V value) {
        return addCriterion(true, buildGreaterThan(column, value));
    }

    default <V> X notGreaterThan(C column, V value) {
        return addCriterion(true, buildGreaterThan(column, value).not());
    }

    default <V> X greaterThanOrEqual(C column, V value) {
        return addCriterion(true, buildGreaterThanOrEqual(column, value));
    }

    default <V> X notGreaterThanOrEqual(C column, V value) {
        return addCriterion(true, buildGreaterThanOrEqual(column, value).not());
    }

    default <V> X lessThan(C column, V value) {
        return addCriterion(true, buildLessThan(column, value));
    }

    default <V> X notLessThan(C column, V value) {
        return addCriterion(true, buildLessThan(column, value).not());
    }

    default <V> X lessThanOrEqual(C column, V value) {
        return addCriterion(true, buildLessThanOrEqual(column, value));
    }

    default <V> X notLessThanOrEqual(C column, V value) {
        return addCriterion(true, buildLessThanOrEqual(column, value).not());
    }

    default <V> X between(C column, V value, V value1) {
        return addCriterion(true, buildBetween(column, value, value1));
    }

    default <V> X notBetween(C column, V value, V value1) {
        return addCriterion(true, buildBetween(column, value, value1).not());
    }

    default <V> X in(C column, Collection<V> values) {
        return addCriterion(true, buildIn(column, values));
    }

    default <V> X in(C column, V[] values) {
        return addCriterion(true, buildIn(column, values));
    }

    default X in(C column, Query<?, ?, ?> query) {
        return addCriterion(true, buildIn(column, query));
    }

    default <V> X notIn(C column, Collection<V> values) {
        return addCriterion(true, buildIn(column, values).not());
    }

    default <V> X notIn(C column, V[] values) {
        return addCriterion(true, buildIn(column, values).not());
    }

    default X like(C column, String value) {
        return addCriterion(true, buildLike(column, value));
    }

    default X notLike(C column, String value) {
        return addCriterion(true, buildLike(column, value).not());
    }

    default X startsWith(C column, String value) {
        return addCriterion(true, buildStartsWith(column, value));
    }

    default X notStartsWith(C column, String value) {
        return addCriterion(true, buildStartsWith(column, value).not());
    }

    default X endsWith(C column, String value) {
        return addCriterion(true, buildEndsWith(column, value));
    }

    default X notEndsWith(C column, String value) {
        return addCriterion(true, buildEndsWith(column, value).not());
    }

    default X contains(C column, String value) {
        return addCriterion(true, buildContains(column, value));
    }

    default X notContains(C column, String value) {
        return addCriterion(true, buildContains(column, value).not());
    }

}
