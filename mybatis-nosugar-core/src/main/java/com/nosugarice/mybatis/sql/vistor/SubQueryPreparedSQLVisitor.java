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

package com.nosugarice.mybatis.sql.vistor;

import com.nosugarice.mybatis.config.DialectContext;
import com.nosugarice.mybatis.criteria.CriteriaQuery;
import com.nosugarice.mybatis.criteria.criterion.ColumnCriterion;
import com.nosugarice.mybatis.criteria.select.QueryStructure;
import com.nosugarice.mybatis.criteria.where.criterion.EqualTo;
import com.nosugarice.mybatis.criteria.where.criterion.GreaterThan;
import com.nosugarice.mybatis.criteria.where.criterion.GreaterThanOrEqual;
import com.nosugarice.mybatis.criteria.where.criterion.InSubQuery;
import com.nosugarice.mybatis.criteria.where.criterion.LessThan;
import com.nosugarice.mybatis.criteria.where.criterion.LessThanOrEqual;
import com.nosugarice.mybatis.criteria.where.criterion.Like;
import com.nosugarice.mybatis.exception.NoSugarException;
import com.nosugarice.mybatis.registry.EntityMetadataRegistry;
import com.nosugarice.mybatis.sql.ParameterBind;
import com.nosugarice.mybatis.sql.Placeholder;
import com.nosugarice.mybatis.sql.ProviderTempLate;
import com.nosugarice.mybatis.sql.ProviderTempLateImpl;
import com.nosugarice.mybatis.sql.SQLConstants;
import com.nosugarice.mybatis.sql.SqlAndParameterBind;
import com.nosugarice.mybatis.sql.render.AbstractRenderingContext;
import com.nosugarice.mybatis.util.Preconditions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class SubQueryPreparedSQLVisitor extends PlaceholderSQLVisitor {

    private static final Set<Class<?>> SUPPER_SUB_QUERY_CRITERION_TYPE = new HashSet<>(
            Arrays.asList(EqualTo.class
                    , GreaterThan.class, GreaterThanOrEqual.class, LessThan.class, LessThanOrEqual.class
                    , InSubQuery.class
                    , Like.class, Like.StartLike.class, Like.EndLike.class, Like.AnyLike.class)
    );

    public SubQueryPreparedSQLVisitor(Class<?> entityClass, ParameterBind parameterBind) {
        super(entityClass, parameterBind, null);
    }

    @Override
    public AbstractRenderingContext createRenderingContext(String prefix) {
        return new SubQueryPreparedRenderingContext();
    }

    @Override
    public SqlAndParameterBind visit(ColumnCriterion<?> criterion) {
        Preconditions.checkArgument(SUPPER_SUB_QUERY_CRITERION_TYPE.contains(criterion.getClass())
                , "子查询不支持比较条件[" + criterion.getClass().getSimpleName() + "].");
        String sql = getRenderingContext().getCriterionExpression(criterion);
        String subQuery = SQLConstants.EMPTY;
        Object value = criterion.getValue();
        if (value instanceof CriteriaQuery) {
            CriteriaQuery<?, ?, ?> subCriteriaQuery = (CriteriaQuery<?, ?, ?>) value;
            ProviderTempLate subProviderTempLate = new ProviderTempLateImpl(
                    EntityMetadataRegistry.getInstance().getEntityMetadata(subCriteriaQuery.getType()), DialectContext.getDialect());
            if (subCriteriaQuery instanceof QueryStructure) {
                ((QueryStructure<?>) subCriteriaQuery).setParameterBind(getParameterBind());
            }
            subQuery = subProviderTempLate.selectList(subCriteriaQuery).getSql();
        }
        sql = sql.replace(Placeholder.SUB_QUERY, subQuery);
        return new SqlAndParameterBind(sql, getParameterBind());
    }

    private class SubQueryPreparedRenderingContext extends AbstractRenderingContext {

        public SubQueryPreparedRenderingContext() {
            super(SubQueryPreparedSQLVisitor.this.getEntityClass());
        }

        @Override
        public String getSingleValuePlaceholder(String column) {
            return "( " + Placeholder.SUB_QUERY + " )";
        }

        @Override
        public String getTwoValuePlaceholder(String column) {
            throw new NoSugarException("子查询不支持[两个值]比较条件.");
        }

        @Override
        public String getListValuePlaceholder(String column) {
            return "( " + Placeholder.SUB_QUERY + " )";
        }

    }

}
