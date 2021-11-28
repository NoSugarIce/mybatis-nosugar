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

import com.nosugarice.mybatis.query.criteria.function.FunctionSelection;
import com.nosugarice.mybatis.query.criterion.GroupCriterion;
import com.nosugarice.mybatis.query.process.HavingCriterion;
import com.nosugarice.mybatis.registry.EntityMetadataRegistry;
import com.nosugarice.mybatis.sql.SqlPart;
import com.nosugarice.mybatis.sql.render.EntitySQLRender;
import com.nosugarice.mybatis.util.Preconditions;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public class JoinCriterion<T1, C1, C> extends AbstractCriteriaQuery<T1, C1> {

    private final CriteriaQuery<?, C> masterQuery;
    private final JoinType joinType;
    private final String table;
    private final String tableAlias;
    private final String column;
    private final String masterColumn;
    private final EntitySQLRender entitySQLRender;

    public JoinCriterion(Builder<T1, C1, C> builder) {
        super(builder.entityClass);
        this.masterQuery = builder.masterQuery;
        this.joinType = builder.joinType;
        this.table = builder.table;
        this.tableAlias = builder.tableAlias;
        this.column = toColumn(builder.property, getFromType());
        this.masterColumn = masterQuery.toColumn(builder.masterProperty, masterQuery.getFromType());
        this.entitySQLRender = new EntitySQLRender.Builder()
                .withTable(table)
                .withSupportDynamicTableName(false)
                .build();
    }

    @Override
    public <T11, C11> JoinCriterion<T11, C11, C1> join(JoinCriterion<T11, C11, C1> joinCriterion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JoinCriterion<T1, C1, C> distinct() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JoinCriterion<T1, C1, C> expand(FunctionSelection functionSelection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CriteriaQuery<T1, C1> addGroupCriterion(GroupCriterion... groupCriterions) {
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    @Override
    public final CriteriaQuery<T1, C1> groupBy(C1... columns) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CriteriaQuery<T1, C1> having(HavingCriterion havingCriterion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JoinCriterion<T1, C1, C> orderBy(C1 column, boolean ascending) {
        throw new UnsupportedOperationException();
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public String getTable() {
        return table;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public String getColumn() {
        return column;
    }

    public String getMasterColumn() {
        return masterColumn;
    }

    public EntitySQLRender getEntitySQLRender() {
        return entitySQLRender;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String toColumn(C1 column, Class<?> entityClass) {
        return masterQuery.toColumn((C) column, entityClass);
    }

    public static class Builder<T1, C1, C> {

        private final CriteriaQuery<?, C> masterQuery;
        private final Class<T1> entityClass;
        private JoinType joinType;
        private String table;
        private String tableAlias;
        private C1 property;
        private C masterProperty;

        public Builder(CriteriaQuery<?, C> masterQuery, Class<T1> entityClass) {
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

        public JoinCriterion<T1, C1, C> build() {
            Preconditions.checkNotNull(entityClass, "被关联表类型未设置.");
            Preconditions.checkNotNull(joinType, "关联类型未设置.");
            Preconditions.checkNotNull(property, "关联表属性为空!");
            Preconditions.checkNotNull(masterProperty, "主表属性为空!");

            EntityMetadataRegistry registry = EntityMetadataRegistry.Holder.getInstance();
            this.table = registry.getTable(entityClass);
            this.tableAlias = SqlPart.tableAlias(table);

            return new JoinCriterion<>(this);
        }

    }

}
