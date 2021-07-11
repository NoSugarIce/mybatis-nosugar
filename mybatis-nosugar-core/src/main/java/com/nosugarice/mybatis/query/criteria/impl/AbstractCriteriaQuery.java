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

package com.nosugarice.mybatis.query.criteria.impl;

import com.nosugarice.mybatis.builder.sql.MetadataCache;
import com.nosugarice.mybatis.query.CriterionSqlUtils;
import com.nosugarice.mybatis.query.DefaultRenderPlaceholderVisitor;
import com.nosugarice.mybatis.query.criteria.CriteriaQuery;
import com.nosugarice.mybatis.query.criterion.GroupCriterionImpl;
import com.nosugarice.mybatis.query.function.FunctionExpression;
import com.nosugarice.mybatis.query.process.Group;
import com.nosugarice.mybatis.query.process.Having;
import com.nosugarice.mybatis.query.process.Order;
import com.nosugarice.mybatis.query.process.Sort;
import com.nosugarice.mybatis.sql.criteria.EntityCriteriaQuery;
import com.nosugarice.mybatis.sql.criterion.Criterion;
import com.nosugarice.mybatis.sql.criterion.GroupCriterion;
import com.nosugarice.mybatis.sql.criterion.PropertyCriterion;
import com.nosugarice.mybatis.sql.criterion.PropertyCriterionVisitor;
import com.nosugarice.mybatis.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public abstract class AbstractCriteriaQuery<T, C> implements EntityCriteriaQuery<T>, CriteriaQuery<T, C> {

    private final Class<?> entityClass;

    private T entity;

    private boolean distinct;

    /** 排除的字段 */
    private Set<String> excludeColumns;

    /** 包含的字段 */
    private Set<String> includeColumns;

    /** 扩展字段 */
    private List<String> expandColumns;

    private Map<String, Object> criterionParameter;

    private List<GroupCriterion> groupCriterions;

    private Group groupBy;

    private Having having;

    private Sort sort;

    public AbstractCriteriaQuery(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public AbstractCriteriaQuery(T entity) {
        this.entityClass = entity.getClass();
        this.entity = entity;
    }

    @Override
    public T getEntity() {
        return entity;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public CriteriaQuery<T, C> distinct() {
        this.distinct = true;
        return this;
    }

    @Override
    public boolean isDistinct() {
        return distinct;
    }

    @SafeVarargs
    @Override
    public final CriteriaQuery<T, C> select(C... columns) {
        if (includeColumns == null) {
            includeColumns = new LinkedHashSet<>(columns.length);
        } else {
            includeColumns.clear();
        }
        includeColumns.addAll(Arrays.asList(conversionColumnName(columns)));
        return this;
    }

    @SafeVarargs
    @Override
    public final CriteriaQuery<T, C> exclude(C... columns) {
        if (excludeColumns == null) {
            excludeColumns = new LinkedHashSet<>(columns.length);
        } else {
            excludeColumns.clear();
        }
        excludeColumns.addAll(Arrays.asList(conversionColumnName(columns)));
        return this;
    }

    @SafeVarargs
    @Override
    public final CriteriaQuery<T, C> expand(FunctionExpression<? extends FunctionExpression<?>>... aggregationFunctions) {
        expandColumns = Arrays.stream(aggregationFunctions)
                .map(FunctionExpression::getSql)
                .collect(Collectors.toList());
        return this;
    }

    @SafeVarargs
    private final String[] conversionColumnName(C... columns) {
        return Arrays.stream(columns)
                .map(convert())
                .toArray(String[]::new);
    }

    @Override
    public final CriteriaQuery<T, C> addCriterion(boolean condition, PropertyCriterion<?>... criterions) {
        if (condition) {
            checkedGroupCriterions();
            GroupCriterion firstGroupCriterion = groupCriterions.get(0);
            firstGroupCriterion.getCriterions().addAll(Stream.of(criterions)
                    .filter(Criterion::condition)
                    .collect(Collectors.toList()));
        }
        return this;
    }

    @Override
    public CriteriaQuery<T, C> addGroupCriterion(GroupCriterion... groupCriterions) {
        checkedGroupCriterions();
        this.groupCriterions.addAll(Stream.of(groupCriterions)
                .filter(Criterion::condition)
                .peek(groupCriterion -> groupCriterion.getCriterions().removeIf(criterion -> !criterion.condition()))
                .collect(Collectors.toList()));
        return this;
    }

    @Override
    public CriteriaQuery<T, C> groupBy(Group groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    @Override
    public CriteriaQuery<T, C> having(Having having) {
        this.having = having;
        return this;
    }

    @Override
    public final CriteriaQuery<T, C> orderBy(Order order) {
        if (this.sort == null) {
            this.sort = new Sort(order);
        } else {
            this.sort.append(order);
        }
        return this;
    }


    @Override
    public boolean isChooseResult() {
        return CollectionUtils.isNotEmpty(includeColumns) || CollectionUtils.isNotEmpty(excludeColumns);
    }

    @Override
    public String getChooseResultSql() {
        if (CollectionUtils.isNotEmpty(excludeColumns)) {
            return MetadataCache.getPropertyCaches(getEntityClass()).stream()
                    .filter(propertyCache -> !excludeColumns.contains(propertyCache.getColumn()))
                    .map(MetadataCache.PropertyCache::getResultItem)
                    .collect(Collectors.joining(",", "", ","));
        }
        if (CollectionUtils.isNotEmpty(includeColumns)) {
            Map<String, MetadataCache.PropertyCache> columnSqlPartMap = MetadataCache.getPropertyCaches(getEntityClass()).stream()
                    .collect(Collectors.toMap(MetadataCache.PropertyCache::getColumn, Function.identity()));
            return includeColumns.stream()
                    .map(column -> columnSqlPartMap.containsKey(column) ? columnSqlPartMap.get(column).getResultItem() : column)
                    .collect(Collectors.joining(",", "", ","));
        }
        return null;
    }

    @Override
    public String getExpandColumnSql() {
        if (CollectionUtils.isNotEmpty(expandColumns)) {
            return expandColumns.stream().collect(Collectors.joining(",", "", ","));
        }
        return null;
    }

    @Override
    public Map<String, Object> getCriterionParameter() {
        return criterionParameter;
    }

    @Override
    public String getCriterionSql() {
        criterionParameter = new HashMap<>();
        PropertyCriterionVisitor<String> visitor = new DefaultRenderPlaceholderVisitor(getEntityClass(), criterionParameter);
        return CriterionSqlUtils.getCriterionSql(visitor, groupCriterions);
    }

    @Override
    public String getGroupSql() {
        return groupBy == null ? null : groupBy.getSql();
    }

    @Override
    public String getHavingSql() {
        return having == null ? null : having.getSql();
    }

    @Override
    public String getSortSql() {
        return sort == null ? null : sort.getSql();
    }

    private void checkedGroupCriterions() {
        if (groupCriterions == null) {
            groupCriterions = new ArrayList<>();
            GroupCriterion groupCriterion = new GroupCriterionImpl();
            groupCriterions.add(groupCriterion);
        }
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }
}
