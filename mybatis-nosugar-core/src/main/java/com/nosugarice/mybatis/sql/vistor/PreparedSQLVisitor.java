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

import com.nosugarice.mybatis.criteria.CriteriaQuery;
import com.nosugarice.mybatis.criteria.criterion.ColumnCriterion;
import com.nosugarice.mybatis.sql.ParameterBind;
import com.nosugarice.mybatis.sql.SqlAndParameterBind;
import com.nosugarice.mybatis.sql.render.AbstractRenderingContext;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class PreparedSQLVisitor extends PlaceholderSQLVisitor {

    public PreparedSQLVisitor(Class<?> entityClass) {
        this(entityClass, null);
    }

    public PreparedSQLVisitor(Class<?> entityClass, ParameterBind parameterBind) {
        super(entityClass, parameterBind, null);
    }

    @Override
    public AbstractRenderingContext createRenderingContext(String prefix) {
        return new PreparedRenderingContext(prefix);
    }

    @Override
    public SqlAndParameterBind visit(ColumnCriterion<?> criterion) {
        Object value = criterion.getValue();
        if (value instanceof CriteriaQuery) {
            return new SubQueryPreparedSQLVisitor(((CriteriaQuery<?, ?, ?>) value).getType(), getParameterBind()).visit(criterion);
        }
        return super.visit(criterion);
    }

    private class PreparedRenderingContext extends PlaceholderRenderingContext {

        public PreparedRenderingContext(String prefix) {
            super(prefix);
        }

        @Override
        public String getPlaceholder(String column, String paramName, String prefix, String assignJdbcType, String assignTypeHandler) {
            return "?";
        }

    }

}
