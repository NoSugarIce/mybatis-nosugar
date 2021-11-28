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

package com.nosugarice.mybatis.sql.render;

import com.nosugarice.mybatis.query.criterion.ColumnCriterion;
import com.nosugarice.mybatis.query.criterion.ColumnCriterionVisitor;
import com.nosugarice.mybatis.query.criterion.ListValueCriterion;
import com.nosugarice.mybatis.sql.ParameterBind;
import com.nosugarice.mybatis.sql.RenderingContext;
import com.nosugarice.mybatis.sql.SqlAndParameterBind;
import com.nosugarice.mybatis.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class PlaceholderVisitor implements ColumnCriterionVisitor<SqlAndParameterBind> {

    private final Class<?> entityClass;
    private final ParameterBind parameterBind;
    private final String prefix;

    public PlaceholderVisitor(Class<?> entityClass, String prefix) {
        this(entityClass, null, prefix);
    }

    public PlaceholderVisitor(Class<?> entityClass, ParameterBind parameterBind, String prefix) {
        this.entityClass = entityClass;
        this.parameterBind = parameterBind == null ? new ParameterBind() : parameterBind;
        this.prefix = prefix;
    }

    @Override
    public SqlAndParameterBind visit(ColumnCriterion<?> columnCriterion) {
        List<String> paramKeys = new ArrayList<>();
        if (columnCriterion.isSingleValue()) {
            String paramKey1 = parameterBind.bindValue(columnCriterion.getValue(), columnCriterion.getColumn(), entityClass).getParameter();
            paramKeys.add(paramKey1);
        }
        if (columnCriterion.isTwoValue()) {
            String paramKey1 = parameterBind.bindValue(columnCriterion.getValue(), columnCriterion.getColumn(), entityClass).getParameter();
            String paramKey2 = parameterBind.bindValue(columnCriterion.getSecondValue(), columnCriterion.getColumn(), entityClass).getParameter();
            paramKeys.add(paramKey1);
            paramKeys.add(paramKey2);
        }
        if (columnCriterion.isListValue()) {
            ListValueCriterion<?, ?> listValueCriterion = (ListValueCriterion<?, ?>) columnCriterion;
            if (CollectionUtils.isNotEmpty(listValueCriterion.getValues())) {
                Collection<?> values = listValueCriterion.getValues();
                for (Object value : values) {
                    String paramKey = parameterBind.bindValue(value, columnCriterion.getColumn(), entityClass).getParameter();
                    paramKeys.add(paramKey);
                }
            }
        }
        RenderingContext renderingContext = createRenderingContext(entityClass, paramKeys.iterator(), prefix);
        String sql = columnCriterion.renderPlaceholder(renderingContext);
        return new SqlAndParameterBind(sql, parameterBind);
    }

    public RenderingContext createRenderingContext(Class<?> entityClass, Iterator<String> paramNameIterator, String prefix) {
        return new PlaceholderRenderingContext(entityClass, paramNameIterator, prefix);
    }

    public ParameterBind getParameterBind() {
        return parameterBind;
    }

}
