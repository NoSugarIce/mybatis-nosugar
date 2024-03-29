/*
 * Copyright 2021-2023 NoSugarIce
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nosugarice.mybatis.criteria.select;

import com.nosugarice.mybatis.criteria.clause.AggFunction;
import com.nosugarice.mybatis.criteria.clause.Operator;
import com.nosugarice.mybatis.criteria.clause.Query;
import com.nosugarice.mybatis.criteria.clause.SQLFunction;
import com.nosugarice.mybatis.criteria.criterion.JoinCriterion;
import com.nosugarice.mybatis.criteria.tocolumn.ToColumn;
import com.nosugarice.mybatis.mapping.Table;
import com.nosugarice.mybatis.registry.EntityMetadataRegistry;
import com.nosugarice.mybatis.sql.TableAliasSequence;
import com.nosugarice.mybatis.util.Preconditions;

import java.util.Collection;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public class JoinCriterionImpl<T1, C1, C> extends AbstractQuery<T1, C1, JoinCriterionImpl<T1, C1, C>>
        implements JoinCriterion<T1, C1, C, JoinCriterionImpl<T1, C1, C>> {

    private final Query<?, C, ?> masterQuery;
    private final JoinType joinType;
    private final String schema;
    private final String table;
    private final String column;
    private final String masterColumn;

    @SuppressWarnings("unchecked")
    public JoinCriterionImpl(Builder<T1, C1, C> builder) {
        super(builder.entityClass, (ToColumn<C1>) builder.masterQuery.getToColumn(), builder.tableAliasSequence);
        this.masterQuery = builder.masterQuery;
        this.joinType = builder.joinType;
        this.schema = builder.schema;
        this.table = builder.table;
        this.column = toColumn(builder.property, getType());
        this.masterColumn = masterQuery.getToColumn().toColumn(builder.masterProperty, masterQuery.getType());
    }

    @Override
    public final <T11, C11> JoinCriterionImpl<T1, C1, C> join(JoinCriterion<T11, C11, C1, ?> joinCriterion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final JoinCriterionImpl<T1, C1, C> distinct() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final JoinCriterionImpl<T1, C1, C> expand(SQLFunction sqlFunction, C1 column, String alias) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JoinCriterionImpl<T1, C1, C> groupBy(Collection<C1> columns) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <V> JoinCriterionImpl<T1, C1, C> having(AggFunction function, C1 column, Operator operator, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final JoinCriterionImpl<T1, C1, C> orderBy(C1 column, boolean ascending) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JoinType getJoinType() {
        return joinType;
    }

    @Override
    public String getSchema() {
        return schema;
    }

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public String getColumn() {
        return column;
    }

    @Override
    public String getMasterColumn() {
        return masterColumn;
    }

    @SuppressWarnings("unchecked")
    public String toColumn(C1 column, Class<?> entityClass) {
        return masterQuery.getToColumn().toColumn((C) column, entityClass);
    }

    public static class Builder<T1, C1, C> {

        private final Query<?, C, ?> masterQuery;
        private final Class<T1> entityClass;
        private JoinType joinType;

        private String schema;
        private String table;
        private C1 property;
        private C masterProperty;

        private TableAliasSequence tableAliasSequence;

        public Builder(Query<?, C, ?> masterQuery, Class<T1> entityClass) {
            this.masterQuery = masterQuery;
            this.entityClass = entityClass;
        }

        public Builder<T1, C1, C> withJoinType(JoinType joinType) {
            this.joinType = joinType;
            return this;
        }

        public Builder<T1, C1, C> withProperty(C1 property) {
            this.property = property;
            return this;
        }

        public Builder<T1, C1, C> withMasterProperty(C masterProperty) {
            this.masterProperty = masterProperty;
            return this;
        }

        public Builder<T1, C1, C> withTableAliasSequence(TableAliasSequence tableAliasSequence) {
            this.tableAliasSequence = tableAliasSequence;
            return this;
        }

        public JoinCriterionImpl<T1, C1, C> build() {
            Preconditions.checkNotNull(entityClass, "被关联表类型未设置.");
            Preconditions.checkNotNull(joinType, "关联类型未设置.");
            Preconditions.checkNotNull(property, "关联表属性为空.");
            Preconditions.checkNotNull(masterProperty, "主表属性为空.");
            Preconditions.checkNotNull(tableAliasSequence, "表别名生成器为空.");

            EntityMetadataRegistry registry = EntityMetadataRegistry.getInstance();
            Table table = registry.getEntityMetadata(entityClass).getRelationalEntity().getTable();
            this.schema = table.getSchema();
            this.table = table.getName();

            return new JoinCriterionImpl<>(this);
        }

    }

}
