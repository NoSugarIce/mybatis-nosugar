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

import com.nosugarice.mybatis.builder.EntityMetadata;
import com.nosugarice.mybatis.query.criteria.function.FunctionSelection;
import com.nosugarice.mybatis.query.criterion.ColumnCriterion;
import com.nosugarice.mybatis.query.criterion.Criterion;
import com.nosugarice.mybatis.query.criterion.GroupCriterion;
import com.nosugarice.mybatis.query.criterion.GroupCriterionImpl;
import com.nosugarice.mybatis.query.process.GroupByCriterion;
import com.nosugarice.mybatis.query.process.HavingCriterion;
import com.nosugarice.mybatis.query.process.OrderByCriterion;
import com.nosugarice.mybatis.query.process.SortCriterion;
import com.nosugarice.mybatis.registry.EntityMetadataRegistry;
import com.nosugarice.mybatis.sql.render.CriteriaQuerySQLRender;
import com.nosugarice.mybatis.sql.render.EntitySQLRender;
import com.nosugarice.mybatis.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public abstract class AbstractCriteriaQuery<T, C> implements CriteriaQuery<T, C> {

    private final Class<T> entityClass;

    private boolean distinct;

    /** 排除的字段 */
    private Set<C> excludeColumns;

    /** 包含的字段 */
    private Set<C> includeColumns;

    /** 扩展字段 */
    private List<FunctionSelection> functionSelections;

    private List<GroupCriterion> groupCriterions;

    private Set<C> groupByColumns;

    private Map<C, Boolean> orderByColumns;

    private HavingCriterion havingCriterion;

    private JoinCriteria<T> joinCriteria;

    private CriteriaQuerySQLRender render;

    protected AbstractCriteriaQuery(Class<T> entityClass) {
        this.entityClass = entityClass;
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
        if (columns == null || columns.length == 0) {
            return this;
        }
        if (this.includeColumns == null) {
            this.includeColumns = new LinkedHashSet<>();
        }
        this.includeColumns.addAll(Arrays.asList(columns));
        return this;
    }

    @SafeVarargs
    @Override
    public final CriteriaQuery<T, C> exclude(C... columns) {
        if (columns == null || columns.length == 0) {
            return this;
        }
        if (this.excludeColumns == null) {
            this.excludeColumns = new LinkedHashSet<>();
        }
        this.excludeColumns.addAll(Arrays.asList(columns));
        return this;
    }

    @Override
    public CriteriaQuery<T, C> expand(FunctionSelection functionSelection) {
        if (functionSelections == null) {
            functionSelections = new ArrayList<>();
        }
        this.functionSelections.add(functionSelection);
        return this;
    }

    @Override
    public Class<T> getFromType() {
        return entityClass;
    }

    @Override
    public CriteriaQuery<T, C> addCriterion(boolean condition, ColumnCriterion<?>... criterions) {
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
    public CriteriaQuery<T, C> groupBy(C... columns) {
        if (columns == null || columns.length == 0) {
            return this;
        }
        if (this.groupByColumns == null) {
            this.groupByColumns = new LinkedHashSet<>();
        }
        this.groupByColumns.addAll(Arrays.asList(columns));
        return this;
    }

    @Override
    public CriteriaQuery<T, C> having(HavingCriterion havingCriterion) {
        this.havingCriterion = havingCriterion;
        return this;
    }

    @Override
    public CriteriaQuery<T, C> orderBy(C column, boolean ascending) {
        if (orderByColumns == null) {
            orderByColumns = new LinkedHashMap<>();
        }
        orderByColumns.put(column, ascending);
        return this;
    }

    @Override
    public <T1, C1> JoinCriterion<T1, C1, C> join(JoinCriterion<T1, C1, C> joinCriterion) {
        if (joinCriteria == null) {
            joinCriteria = new JoinCriteria<>(getFromType());
        }
        joinCriteria.addJoinCriterion(joinCriterion);
        return joinCriterion;
    }

    @Override
    public Optional<List<ColumnSelection>> getColumnSelections() {
        if (includeColumns == null && excludeColumns == null) {
            return Optional.empty();
        }
        EntityMetadata entityMetadata = EntityMetadataRegistry.Holder.getInstance().getEntityMetadata(entityClass);

        Optional<List<ColumnSelection>> columnSelections = Optional.ofNullable(includeColumns)
                .map(this::conversionColumn)
                .map(columns -> Arrays.stream(columns).map(ColumnSelection::new).collect(Collectors.toList()));

        if (!columnSelections.isPresent() && CollectionUtils.isNotEmpty(excludeColumns)) {
            Set<String> excludeColumnSet = Optional.of(excludeColumns)
                    .map(this::conversionColumn)
                    .map(columns -> Arrays.stream(columns).collect(Collectors.toSet())).orElse(null);
            columnSelections = Optional.of(entityMetadata.getColumns())
                    .map(columns -> columns.stream().filter(column -> !excludeColumnSet.contains(column))
                            .map(ColumnSelection::new).collect(Collectors.toList()));
        }
        columnSelections.ifPresent(selections -> selections.forEach(selection
                -> selection.alias(Optional.ofNullable(
                entityMetadata.getPropertyByColumnName(selection.getColumn()).getName()).orElse(null))));
        return columnSelections;
    }

    @Override
    public Optional<List<FunctionSelection>> getFunctionSelections() {
        return Optional.ofNullable(functionSelections);
    }

    @Override
    public Optional<List<GroupCriterion>> getGroupCriterions() {
        return Optional.ofNullable(groupCriterions);
    }

    @Override
    public Optional<GroupByCriterion> getGroupBy() {
        return Optional.ofNullable(groupByColumns).map(columns -> new GroupByCriterion(conversionColumn(columns)));
    }

    @Override
    public Optional<HavingCriterion> getHaving() {
        return Optional.ofNullable(havingCriterion);
    }

    @Override
    public Optional<SortCriterion> getSort() {
        return Optional.ofNullable(orderByColumns).map(columns -> {
            List<OrderByCriterion> orderByCriteria = columns.entrySet().stream()
                    .map(entry -> new OrderByCriterion(toColumn(entry.getKey(), entityClass), entry.getValue()))
                    .collect(Collectors.toList());
            return new SortCriterion(orderByCriteria);
        });
    }

    @Override
    public Optional<JoinCriteria<T>> getJoinCriteria() {
        return Optional.ofNullable(joinCriteria);
    }

    private String[] conversionColumn(Collection<C> columns) {
        return columns.stream()
                .map(column -> toColumn(column, this.getFromType()))
                .toArray(String[]::new);
    }

    private void checkedGroupCriterions() {
        if (groupCriterions == null) {
            groupCriterions = new ArrayList<>();
            GroupCriterion groupCriterion = new GroupCriterionImpl();
            groupCriterions.add(groupCriterion);
        }
    }

    @Override
    public CriteriaQuerySQLRender getRender(EntitySQLRender sqlRender) {
        if (render == null) {
            render = new CriteriaQuerySQLRender(this, sqlRender);
        }
        return render;
    }


}
