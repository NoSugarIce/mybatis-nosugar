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

package com.nosugarice.mybatis.sql.vistor;

import com.nosugarice.mybatis.criteria.criterion.ColumnCriterion;
import com.nosugarice.mybatis.criteria.criterion.CriterionSQLVisitor;
import com.nosugarice.mybatis.criteria.criterion.GroupCriterion;
import com.nosugarice.mybatis.criteria.where.criterion.ListValueCriterion;
import com.nosugarice.mybatis.sql.ParameterBind;
import com.nosugarice.mybatis.sql.SqlAndParameterBind;
import com.nosugarice.mybatis.sql.render.AbstractRenderingContext;
import com.nosugarice.mybatis.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

import static com.nosugarice.mybatis.sql.SQLConstants.AND;
import static com.nosugarice.mybatis.sql.SQLConstants.EMPTY;
import static com.nosugarice.mybatis.sql.SQLConstants.SPACE;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class PlaceholderSQLVisitor implements CriterionSQLVisitor<SqlAndParameterBind> {

    private final Class<?> entityClass;
    private final ParameterBind parameterBind;
    private final AbstractRenderingContext renderingContext;

    public PlaceholderSQLVisitor(Class<?> entityClass, ParameterBind parameterBind, String prefix) {
        this.entityClass = entityClass;
        this.parameterBind = parameterBind == null ? new ParameterBind() : parameterBind;
        this.renderingContext = createRenderingContext(prefix);
    }

    @Override
    public SqlAndParameterBind visit(ColumnCriterion<?> criterion) {
        List<String> paramKeys = new ArrayList<>();
        if (criterion.isSingleValue()) {
            String paramKey1 = parameterBind.bindValue(criterion.getValue(), criterion.getColumn(), entityClass).getParameter();
            paramKeys.add(paramKey1);
        } else if (criterion.isTwoValue()) {
            String paramKey1 = parameterBind.bindValue(criterion.getValue(), criterion.getColumn(), entityClass).getParameter();
            String paramKey2 = parameterBind.bindValue(criterion.getSecondValue(), criterion.getColumn(), entityClass).getParameter();
            paramKeys.add(paramKey1);
            paramKeys.add(paramKey2);
        } else if (criterion.isListValue()) {
            ListValueCriterion<?, ?> listValueCriterion = (ListValueCriterion<?, ?>) criterion;
            if (CollectionUtils.isNotEmpty(listValueCriterion.getValue())) {
                Collection<?> values = listValueCriterion.getValue();
                for (Object value : values) {
                    String paramKey = parameterBind.bindValue(value, criterion.getColumn(), entityClass).getParameter();
                    paramKeys.add(paramKey);
                }
            }
        }
        renderingContext.setParamNameIterator(paramKeys.iterator());
        String sql = renderingContext.getCriterionExpression(criterion);
        return new SqlAndParameterBind(sql, parameterBind);
    }

    @Override
    public SqlAndParameterBind visit(GroupCriterion criterion) {
        setSQLStrategy(criterion);
        return new SqlAndParameterBind(criterion.getSql(), parameterBind);
    }

    @Override
    public String visitResultHandle(SqlAndParameterBind sqlAndParameterBind) {
        return sqlAndParameterBind.getSql();
    }

    public AbstractRenderingContext createRenderingContext(String prefix) {
        return new PlaceholderRenderingContext(prefix);
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public ParameterBind getParameterBind() {
        return parameterBind;
    }

    public AbstractRenderingContext getRenderingContext() {
        return renderingContext;
    }

    protected class PlaceholderRenderingContext extends AbstractRenderingContext {

        private final String prefix;

        public PlaceholderRenderingContext(String prefix) {
            super(PlaceholderSQLVisitor.this.entityClass);
            this.prefix = prefix;
        }

        @Override
        public String getSingleValuePlaceholder(String column) {
            if (paramNameIterator.hasNext()) {
                String paramName = paramNameIterator.next();
                paramNameIterator.remove();
                return getPlaceholder(column, paramName, prefix, null, null);
            }
            return EMPTY;
        }

        @Override
        public String getTwoValuePlaceholder(String column) {
            return getSingleValuePlaceholder(column) + SPACE + AND + SPACE + getSingleValuePlaceholder(column);
        }

        @Override
        public String getListValuePlaceholder(String column) {
            StringJoiner stringJoiner = new StringJoiner(", ", "(", ")");
            while (paramNameIterator.hasNext()) {
                String paramName = paramNameIterator.next();
                paramNameIterator.remove();
                stringJoiner.add(getPlaceholder(column, paramName, prefix, null, null));
            }
            return stringJoiner.toString();
        }
    }

}
