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

import com.nosugarice.mybatis.criteria.ToColumn;
import com.nosugarice.mybatis.criteria.where.criterion.Between;
import com.nosugarice.mybatis.criteria.where.criterion.Empty;
import com.nosugarice.mybatis.criteria.where.criterion.EqualTo;
import com.nosugarice.mybatis.criteria.where.criterion.GreaterThan;
import com.nosugarice.mybatis.criteria.where.criterion.GreaterThanOrEqual;
import com.nosugarice.mybatis.criteria.where.criterion.GroupCriterionImpl;
import com.nosugarice.mybatis.criteria.where.criterion.In;
import com.nosugarice.mybatis.criteria.where.criterion.IsNull;
import com.nosugarice.mybatis.criteria.where.criterion.LessThan;
import com.nosugarice.mybatis.criteria.where.criterion.LessThanOrEqual;
import com.nosugarice.mybatis.criteria.where.criterion.Like;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public abstract class AbstractWhere<C, X extends Where<C, X>> implements Where<C, X>, CriterionBuilder<C>, WhereStructure {

    private final Class<?> entityClass;
    private final ToColumn<C> toColumn;

    private final GroupCriterion groupCriterion = new GroupCriterionImpl();

    protected AbstractWhere(Class<?> entityClass, ToColumn<C> toColumn) {
        this.entityClass = entityClass;
        this.toColumn = toColumn;
    }

    /**
     * 转换真实列名
     *
     * @param column
     * @return
     */
    protected String toColumn(C column) {
        return toColumn.toColumn(column, entityClass);
    }

    @Override
    public Class<?> getType() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public X addCriterion(boolean condition, Criterion... criterions) {
        if (condition && criterions != null && criterions.length > 0) {
            groupCriterion.append(criterions);
        }
        return (X) this;
    }

    @Override
    public X or(Criterion... criterions) {
        return addCriterion(new GroupCriterionImpl(criterions).byOr());
    }

    @Override
    public IsNull buildIsNull(C column) {
        return new IsNull(toColumn(column));
    }

    @Override
    public Empty buildIsEmpty(C column) {
        return new Empty(toColumn(column));
    }

    @Override
    public <V> EqualTo<V> buildEqualTo(C column, V value) {
        return new EqualTo<>(toColumn(column), value);
    }

    @Override
    public <V> GreaterThan<V> buildGreaterThan(C column, V value) {
        return new GreaterThan<>(toColumn(column), value);
    }

    @Override
    public <V> GreaterThanOrEqual<V> buildGreaterThanOrEqual(C column, V value) {
        return new GreaterThanOrEqual<>(toColumn(column), value);
    }

    @Override
    public <V> LessThan<V> buildLessThan(C column, V value) {
        return new LessThan<>(toColumn(column), value);
    }

    @Override
    public <V> LessThanOrEqual<V> buildLessThanOrEqual(C column, V value) {
        return new LessThanOrEqual<>(toColumn(column), value);
    }

    @Override
    public <V> Between<V> buildBetween(C column, V value, V value1) {
        return new Between<>(toColumn(column), value, value1);
    }

    @Override
    public <V> In<V> buildIn(C column, Collection<V> values) {
        return new In<>(toColumn(column), values);
    }

    @Override
    public <V> In<V> buildIn(C column, V[] values) {
        return buildIn(column, Arrays.asList(values));
    }

    @Override
    public Like buildLike(C column, String value) {
        return new Like(toColumn(column), value);
    }

    @Override
    public Like buildStartsWith(C column, String value) {
        return new Like.StartLike(toColumn(column), value);
    }

    @Override
    public Like buildEndsWith(C column, String value) {
        return new Like.EndLike(toColumn(column), value);
    }

    @Override
    public Like buildContains(C column, String value) {
        return new Like.AnyLike(toColumn(column), value);
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public ToColumn<C> getConvertToColumn() {
        return toColumn;
    }

    @Override
    public Optional<GroupCriterion> getCriterion() {
        return Optional.of(groupCriterion);
    }
}
