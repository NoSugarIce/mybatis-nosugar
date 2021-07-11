/*
 *    Copyright 2021 NoSugarIce
 *
 *    Licensed under the Apache License, Version 2.0 (the "License";
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

import java.util.Collection;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public interface PredicateCondition<T, C> extends Where<T, C>, CriterionBuilder<C> {

    default CriteriaQuery<T, C> isNull(C column) {
        return addCriterion(true, buildIsNull(column));
    }

    default CriteriaQuery<T, C> notNull(C column) {
        return addCriterion(true, buildNotNull(column));
    }

    default CriteriaQuery<T, C> empty(C column) {
        return addCriterion(true, buildEmpty(column));
    }

    default CriteriaQuery<T, C> notEmpty(C column) {
        return addCriterion(true, buildNotEmpty(column));
    }

    default <V> CriteriaQuery<T, C> equal(C column, V value) {
        return addCriterion(true, buildEqual(column, value));
    }

    default <V> CriteriaQuery<T, C> notEqual(C column, V value) {
        return addCriterion(true, buildNotEqual(column, value));
    }

    default <V> CriteriaQuery<T, C> greaterThan(C column, V value) {
        return addCriterion(true, buildGreaterThan(column, value));
    }

    default <V> CriteriaQuery<T, C> notGreaterThan(C column, V value) {
        return addCriterion(true, buildNotGreaterThan(column, value));
    }

    default <V> CriteriaQuery<T, C> greaterThanOrEqualTo(C column, V value) {
        return addCriterion(true, buildGreaterThan(column, value));
    }

    default <V> CriteriaQuery<T, C> notGreaterThanOrEqualTo(C column, V value) {
        return addCriterion(true, buildNotGreaterThan(column, value));
    }

    default <V> CriteriaQuery<T, C> lessThan(C column, V value) {
        return addCriterion(true, buildLessThan(column, value));
    }

    default <V> CriteriaQuery<T, C> notLessThan(C column, V value) {
        return addCriterion(true, buildNotLessThan(column, value));
    }

    default <V> CriteriaQuery<T, C> lessThanOrEqualTo(C column, V value) {
        return addCriterion(true, buildLessThanOrEqualTo(column, value));
    }

    default <V> CriteriaQuery<T, C> notLessThanOrEqualTo(C column, V value) {
        return addCriterion(true, buildNotLessThanOrEqualTo(column, value));
    }

    default <V> CriteriaQuery<T, C> between(C column, V value, V value1) {
        return addCriterion(true, buildBetween(column, value, value1));
    }

    default <V> CriteriaQuery<T, C> notBetween(C column, V value, V value1) {
        return addCriterion(true, buildNotBetween(column, value, value1));
    }

    default <V> CriteriaQuery<T, C> in(C column, V[] values) {
        return addCriterion(true, buildIn(column, values));
    }

    default <V> CriteriaQuery<T, C> in(C column, Collection<V> values) {
        return addCriterion(true, buildIn(column, values));
    }

    default <V> CriteriaQuery<T, C> notIn(C column, V[] values) {
        return addCriterion(true, buildNotIn(column, values));
    }

    default <V> CriteriaQuery<T, C> notIn(C column, Collection<V> values) {
        return addCriterion(true, buildNotIn(column, values));
    }

    default CriteriaQuery<T, C> like(C column, String value) {
        return addCriterion(true, buildLike(column, value));
    }

    default CriteriaQuery<T, C> notLike(C column, String value) {
        return addCriterion(true, buildNotLike(column, value));
    }

    default CriteriaQuery<T, C> likeBefore(C column, String value) {
        return addCriterion(true, buildLikeBefore(column, value));
    }

    default CriteriaQuery<T, C> notLikeBefore(C column, String value) {
        return addCriterion(true, buildNotLikeBefore(column, value));
    }

    default CriteriaQuery<T, C> likeAfter(C column, String value) {
        return addCriterion(true, buildLikeAfter(column, value));
    }

    default CriteriaQuery<T, C> notLikeAfter(C column, String value) {
        return addCriterion(true, buildNotLikeAfter(column, value));
    }

}
