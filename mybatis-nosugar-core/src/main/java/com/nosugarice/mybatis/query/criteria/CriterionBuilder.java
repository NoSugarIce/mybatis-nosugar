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
 * @author dingjingyang@foxmail.com(dingjingyang)
 * @date 2021/7/4
 */
public interface CriterionBuilder<C> extends ConvertToColumn<C> {

    default <V> Null<V> buildIsNull(C column) {
        return new Null<>(toColumn(column));
    }

    default <V> NotNull<V> buildNotNull(C column) {
        return new NotNull<>(toColumn(column));
    }

    default Empty buildEmpty(C column) {
        return new Empty(toColumn(column));
    }

    default NotEmpty buildNotEmpty(C column) {
        return new NotEmpty(toColumn(column));
    }

    default <V> Equal<V> buildEqual(C column, V value) {
        return new Equal<>(toColumn(column), value);
    }

    default <V> NotEqual<V> buildNotEqual(C column, V value) {
        return new NotEqual<>(toColumn(column), value);
    }

    default <V> GreaterThan<V> buildGreaterThan(C column, V value) {
        return new GreaterThan<>(toColumn(column), value);
    }

    default <V> NotGreaterThan<V> buildNotGreaterThan(C column, V value) {
        return new NotGreaterThan<>(toColumn(column), value);
    }

    default <V> GreaterThan<V> buildGreaterThanOrEqualTo(C column, V value) {
        return new GreaterThan<>(toColumn(column), value);
    }

    default <V> NotGreaterThan<V> buildNotGreaterThanOrEqualTo(C column, V value) {
        return new NotGreaterThan<>(toColumn(column), value);
    }

    default <V> LessThan<V> buildLessThan(C column, V value) {
        return new LessThan<>(toColumn(column), value);
    }

    default <V> NotLessThan<V> buildNotLessThan(C column, V value) {
        return new NotLessThan<>(toColumn(column), value);
    }

    default <V> LessThanOrEqual<V> buildLessThanOrEqualTo(C column, V value) {
        return new LessThanOrEqual<>(toColumn(column), value);
    }

    default <V> NotLessThanOrEqual<V> buildNotLessThanOrEqualTo(C column, V value) {
        return new NotLessThanOrEqual<>(toColumn(column), value);
    }

    default <V> Between<V> buildBetween(C column, V value, V value1) {
        return new Between<>(toColumn(column), value, value1);
    }

    default <V> NotBetween<V> buildNotBetween(C column, V value, V value1) {
        return new NotBetween<>(toColumn(column), value, value1);
    }

    default <V> In<V> buildIn(C column, V[] values) {
        return new In<>(toColumn(column), values);
    }

    default <V> In<V> buildIn(C column, Collection<V> values) {
        return new In<>(toColumn(column), values);
    }

    default <V> NotIn<V> buildNotIn(C column, V[] values) {
        return new NotIn<>(toColumn(column), values);
    }

    default <V> NotIn<V> buildNotIn(C column, Collection<V> values) {
        return new NotIn<>(toColumn(column), values);
    }

    default Like buildLike(C column, String value) {
        return new Like(toColumn(column), value, Like.MatchMode.ANYWHERE);
    }

    default NotLike buildNotLike(C column, String value) {
        return new NotLike(toColumn(column), value, Like.MatchMode.ANYWHERE);
    }

    default Like buildLikeBefore(C column, String value) {
        return new Like(toColumn(column), value, Like.MatchMode.START);
    }

    default NotLike buildNotLikeBefore(C column, String value) {
        return new NotLike(toColumn(column), value, Like.MatchMode.START);
    }

    default Like buildLikeAfter(C column, String value) {
        return new Like(toColumn(column), value, Like.MatchMode.END);
    }

    default NotLike buildNotLikeAfter(C column, String value) {
        return new NotLike(toColumn(column), value, Like.MatchMode.END);
    }

}
