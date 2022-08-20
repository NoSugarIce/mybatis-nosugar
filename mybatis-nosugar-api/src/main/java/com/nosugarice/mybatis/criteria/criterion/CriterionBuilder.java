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

package com.nosugarice.mybatis.criteria.criterion;

import com.nosugarice.mybatis.criteria.Query;

import java.util.Collection;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/4
 */
public interface CriterionBuilder<C> {

    ColumnCriterion<Object> buildIsNull(C column);

    ColumnCriterion<String> buildIsEmpty(C column);

    <V> ColumnCriterion<V> buildEqualTo(C column, V value);

    <V> ColumnCriterion<V> buildGreaterThan(C column, V value);

    <V> ColumnCriterion<V> buildGreaterThanOrEqual(C column, V value);

    <V> ColumnCriterion<V> buildLessThan(C column, V value);

    <V> ColumnCriterion<V> buildLessThanOrEqual(C column, V value);

    <V> ColumnCriterion<V> buildBetween(C column, V value, V value1);

    <V> ColumnCriterion<Collection<V>> buildIn(C column, Collection<V> values);

    <V> ColumnCriterion<Collection<V>> buildIn(C column, V[] values);

    ColumnCriterion<Query<?, ?, ?>> buildIn(C column, Query<?, ?, ?> query);

    ColumnCriterion<String> buildLike(C column, String value);

    ColumnCriterion<String> buildStartsWith(C column, String value);

    ColumnCriterion<String> buildEndsWith(C column, String value);

    ColumnCriterion<String> buildContains(C column, String value);

}
