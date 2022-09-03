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

package com.nosugarice.mybatis.criteria.select;

import com.nosugarice.mybatis.config.EntityMetadata;
import com.nosugarice.mybatis.criteria.clause.AggFunction;
import com.nosugarice.mybatis.criteria.clause.Operator;
import com.nosugarice.mybatis.criteria.clause.Query;
import com.nosugarice.mybatis.criteria.clause.SQLFunction;
import com.nosugarice.mybatis.criteria.criterion.JoinCriterion;
import com.nosugarice.mybatis.criteria.criterion.JoinCriterion.JoinType;
import com.nosugarice.mybatis.criteria.tocolumn.ToColumn;
import com.nosugarice.mybatis.criteria.where.AbstractWhere;
import com.nosugarice.mybatis.domain.Page;
import com.nosugarice.mybatis.registry.EntityMetadataRegistry;
import com.nosugarice.mybatis.sql.ParameterBind;
import com.nosugarice.mybatis.sql.render.EntitySQLRender;
import com.nosugarice.mybatis.sql.render.QuerySQLRender;
import com.nosugarice.mybatis.util.CollectionUtils;
import org.apache.ibatis.session.RowBounds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public abstract class AbstractQuery<T, C, X extends Query<T, C, X>> extends AbstractWhere<C, X>
        implements Query<T, C, X>, QueryStructure<T> {

    private boolean simple;

    private boolean distinct;

    /** 排除的字段 */
    private Set<C> excludeColumns;

    /** 包含的字段 */
    private Set<C> includeColumns;

    /** 扩展字段 */
    private List<FunctionSelection> functionSelections;

    private String countColumn;

    private GroupByCriterion groupByCriterion;

    private HavingCriterion havingCriterion;

    private OrderByCriterion orderByCriterion;

    private RowBounds rowBounds;

    private JoinCriteria<T> joinCriteria;

    private boolean forUpdate;

    private QuerySQLRender render;

    private ParameterBind parameterBind;

    protected AbstractQuery(Class<?> entityClass, ToColumn<C> toColumn) {
        super(entityClass, toColumn);
    }

    @Override
    public ToColumn<C> getToColumn() {
        return super.getConvertToColumn();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getType() {
        return (Class<T>) getEntityClass();
    }

    @Override
    public boolean isSimple() {
        return simple;
    }

    public X simple() {
        this.simple = true;
        return getThis();
    }

    @Override
    public X distinct() {
        this.distinct = true;
        return getThis();
    }

    @Override
    public boolean isDistinct() {
        return distinct;
    }

    @Override
    public X select(Collection<C> columns) {
        if (CollectionUtils.isEmpty(columns)) {
            return getThis();
        }
        if (this.includeColumns == null) {
            this.includeColumns = new LinkedHashSet<>();
        }
        this.includeColumns.addAll(columns);
        return getThis();
    }

    @Override
    public X exclude(Collection<C> columns) {
        if (CollectionUtils.isEmpty(columns)) {
            return getThis();
        }
        if (this.excludeColumns == null) {
            this.excludeColumns = new LinkedHashSet<>();
        }
        this.excludeColumns.addAll(columns);
        return getThis();
    }

    @Override
    public X expand(SQLFunction sqlFunction, C column, String alias) {
        if (functionSelections == null) {
            functionSelections = new ArrayList<>();
        }
        this.functionSelections.add(new FunctionSelection(sqlFunction.getName(), toColumn(column)).alias(alias));
        return getThis();
    }

    @Override
    public X count(C column) {
        this.countColumn = toColumn(column);
        return getThis();
    }

    @Override
    public X groupBy(Collection<C> columns) {
        if (CollectionUtils.isEmpty(columns)) {
            return getThis();
        }
        this.groupByCriterion = new GroupByCriterion(conversionColumn(columns));
        return getThis();
    }

    @Override
    public <V> X having(AggFunction function, C column, Operator operator, V value) {
        if (havingCriterion == null) {
            havingCriterion = new HavingCriterion();
        }
        havingCriterion.addCriterion(function, toColumn(column), operator, value);
        return getThis();
    }

    @Override
    public X orderBy(C column, boolean ascending) {
        if (orderByCriterion == null) {
            orderByCriterion = new OrderByCriterion();
        }
        orderByCriterion.addCriterion(toColumn(column), ascending);
        return getThis();
    }

    @Override
    public X limit(Page<?> page) {
        this.rowBounds = new RowBounds(page.getOffset(), page.getSize());
        return getThis();
    }

    @Override
    public X limit(int offset, int limit) {
        this.rowBounds = new RowBounds(offset, limit);
        return getThis();
    }

    @Override
    public X forUpdate() {
        this.forUpdate = true;
        return getThis();
    }

    @Override
    public <T1, C1> X join(JoinCriterion<T1, C1, C, ?> joinCriterion) {
        if (joinCriteria == null) {
            joinCriteria = new JoinCriteria<>(getType());
        }
        joinCriteria.addJoinCriterion(joinCriterion);
        return getThis();
    }

    @Override
    public <T1, C1> JoinCriterion<T1, C1, C, ?> buildJoin(JoinType joinType, Class<T1> entityClass, C1 property, C masterProperty) {
        return new JoinCriterionImpl.Builder<T1, C1, C>(this, entityClass)
                .withJoinType(joinType)
                .withProperty(property)
                .withMasterProperty(masterProperty)
                .build();
    }

    @Override
    public Optional<List<ColumnSelection>> getColumnSelections() {
        if (includeColumns == null && excludeColumns == null) {
            return Optional.empty();
        }
        EntityMetadata entityMetadata = EntityMetadataRegistry.getInstance().getEntityMetadata(getType());
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
        columnSelections.ifPresent(selections -> selections.stream()
                .filter(selection -> !selection.getColumn().equals(entityMetadata.getPropertyByColumnName(selection.getColumn()).getName()))
                .forEach(selection -> selection.alias(entityMetadata.getPropertyByColumnName(selection.getColumn()).getName())));
        return columnSelections;
    }

    @Override
    public Optional<List<FunctionSelection>> getFunctionSelections() {
        return Optional.ofNullable(functionSelections);
    }

    @Override
    public Optional<String> getCountColumn() {
        return Optional.ofNullable(countColumn);
    }

    @Override
    public Optional<GroupByCriterion> getGroupBy() {
        return Optional.ofNullable(groupByCriterion);
    }

    @Override
    public Optional<HavingCriterion> getHaving() {
        return Optional.ofNullable(havingCriterion);
    }

    @Override
    public Optional<OrderByCriterion> getOrderBy() {
        return Optional.ofNullable(orderByCriterion);
    }

    @Override
    public Optional<RowBounds> getLimit() {
        return Optional.ofNullable(rowBounds);
    }

    @Override
    public boolean isForUpdate() {
        return forUpdate;
    }

    @Override
    public Optional<JoinCriteria<T>> getJoinCriteria() {
        return Optional.ofNullable(joinCriteria);
    }

    private String[] conversionColumn(Collection<C> columns) {
        return columns.stream()
                .map(this::toColumn)
                .toArray(String[]::new);
    }

    @Override
    public QuerySQLRender getRender(EntitySQLRender sqlRender) {
        if (render == null) {
            render = new QuerySQLRender(this);
        }
        return render;
    }

    @Override
    public ParameterBind getParameterBind() {
        return parameterBind;
    }

    @Override
    public void setParameterBind(ParameterBind parameterBind) {
        this.parameterBind = parameterBind;
    }

}
