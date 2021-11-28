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

import com.nosugarice.mybatis.query.criterion.Between;
import com.nosugarice.mybatis.query.criterion.Empty;
import com.nosugarice.mybatis.query.criterion.Equal;
import com.nosugarice.mybatis.query.criterion.GreaterThan;
import com.nosugarice.mybatis.query.criterion.In;
import com.nosugarice.mybatis.query.criterion.LessThan;
import com.nosugarice.mybatis.query.criterion.LessThanOrEqual;
import com.nosugarice.mybatis.query.criterion.Like;
import com.nosugarice.mybatis.query.criterion.NotBetween;
import com.nosugarice.mybatis.query.criterion.NotEmpty;
import com.nosugarice.mybatis.query.criterion.NotEqual;
import com.nosugarice.mybatis.query.criterion.NotGreaterThan;
import com.nosugarice.mybatis.query.criterion.NotIn;
import com.nosugarice.mybatis.query.criterion.NotLessThan;
import com.nosugarice.mybatis.query.criterion.NotLessThanOrEqual;
import com.nosugarice.mybatis.query.criterion.NotLike;
import com.nosugarice.mybatis.query.criterion.NotNull;
import com.nosugarice.mybatis.query.criterion.Null;

import java.util.Collection;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/4
 */
public interface CriterionBuilder<T, C> extends From<T>, ConvertToColumn<C> {

    default <V> Null<V> buildIsNull(C column) {
        return new Null<>(toColumn(column, getFromType()));
    }

    default <V> NotNull<V> buildNotNull(C column) {
        return new NotNull<>(toColumn(column, getFromType()));
    }

    default Empty buildEmpty(C column) {
        return new Empty(toColumn(column, getFromType()));
    }

    default NotEmpty buildNotEmpty(C column) {
        return new NotEmpty(toColumn(column, getFromType()));
    }

    default <V> Equal<V> buildEqualTo(C column, V value) {
        return new Equal<>(toColumn(column, getFromType()), value);
    }

    default <V> NotEqual<V> buildNotEqualTo(C column, V value) {
        return new NotEqual<>(toColumn(column, getFromType()), value);
    }

    default <V> GreaterThan<V> buildGreaterThan(C column, V value) {
        return new GreaterThan<>(toColumn(column, getFromType()), value);
    }

    default <V> NotGreaterThan<V> buildNotGreaterThan(C column, V value) {
        return new NotGreaterThan<>(toColumn(column, getFromType()), value);
    }

    default <V> GreaterThan<V> buildGreaterThanOrEqualTo(C column, V value) {
        return new GreaterThan<>(toColumn(column, getFromType()), value);
    }

    default <V> NotGreaterThan<V> buildNotGreaterThanOrEqualTo(C column, V value) {
        return new NotGreaterThan<>(toColumn(column, getFromType()), value);
    }

    default <V> LessThan<V> buildLessThan(C column, V value) {
        return new LessThan<>(toColumn(column, getFromType()), value);
    }

    default <V> NotLessThan<V> buildNotLessThan(C column, V value) {
        return new NotLessThan<>(toColumn(column, getFromType()), value);
    }

    default <V> LessThanOrEqual<V> buildLessThanOrEqualTo(C column, V value) {
        return new LessThanOrEqual<>(toColumn(column, getFromType()), value);
    }

    default <V> NotLessThanOrEqual<V> buildNotLessThanOrEqualTo(C column, V value) {
        return new NotLessThanOrEqual<>(toColumn(column, getFromType()), value);
    }

    default <V> Between<V> buildBetween(C column, V value, V value1) {
        return new Between<>(toColumn(column, getFromType()), value, value1);
    }

    default <V> NotBetween<V> buildNotBetween(C column, V value, V value1) {
        return new NotBetween<>(toColumn(column, getFromType()), value, value1);
    }

    default <V> In<V> buildIn(C column, V[] values) {
        return new In<>(toColumn(column, getFromType()), values);
    }

    default <V> In<V> buildIn(C column, Collection<V> values) {
        return new In<>(toColumn(column, getFromType()), values);
    }

    default <V> NotIn<V> buildNotIn(C column, V[] values) {
        return new NotIn<>(toColumn(column, getFromType()), values);
    }

    default <V> NotIn<V> buildNotIn(C column, Collection<V> values) {
        return new NotIn<>(toColumn(column, getFromType()), values);
    }

    default Like buildLike(C column, String value) {
        return new Like(toColumn(column, getFromType()), value, Like.MatchMode.ANYWHERE);
    }

    default NotLike buildNotLike(C column, String value) {
        return new NotLike(toColumn(column, getFromType()), value, Like.MatchMode.ANYWHERE);
    }

    default Like buildLikeBefore(C column, String value) {
        return new Like(toColumn(column, getFromType()), value, Like.MatchMode.START);
    }

    default NotLike buildNotLikeBefore(C column, String value) {
        return new NotLike(toColumn(column, getFromType()), value, Like.MatchMode.START);
    }

    default Like buildLikeAfter(C column, String value) {
        return new Like(toColumn(column, getFromType()), value, Like.MatchMode.END);
    }

    default NotLike buildNotLikeAfter(C column, String value) {
        return new NotLike(toColumn(column, getFromType()), value, Like.MatchMode.END);
    }

}
