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

package com.nosugarice.mybatis.criteria.where;

import java.util.Collection;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/4
 */
public interface CriterionBuilder<C> {

    ColumnCriterion<Object> buildNull(C column);

    ColumnCriterion<String> buildEmpty(C column);

    <V> ColumnCriterion<V> buildEqualTo(C column, V value);

    <V> ColumnCriterion<V> buildGreaterThan(C column, V value);

    <V> ColumnCriterion<V> buildGreaterThanOrEqualTo(C column, V value);

    <V> ColumnCriterion<V> buildLessThan(C column, V value);

    <V> ColumnCriterion<V> buildLessThanOrEqualTo(C column, V value);

    <V> ColumnCriterion<V> buildBetween(C column, V value, V value1);

    <V> ColumnCriterion<Collection<V>> buildIn(C column, Collection<V> values);

    <V> ColumnCriterion<Collection<V>> buildIn(C column, V[] values);

    ColumnCriterion<String> buildLike(C column, String value);

    ColumnCriterion<String> buildLikeBefore(C column, String value);

    ColumnCriterion<String> buildLikeAfter(C column, String value);

    ColumnCriterion<String> buildLikeAny(C column, String value);

}