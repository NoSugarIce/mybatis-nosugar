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

import com.nosugarice.mybatis.criteria.criterion.ColumnCriterion;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.registry.EntityMetadataRegistry;
import com.nosugarice.mybatis.sql.SQLConstants;
import com.nosugarice.mybatis.sql.SQLPart;
import com.nosugarice.mybatis.util.StringUtils;

import java.util.Iterator;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public abstract class AbstractRenderingContext {

    protected final Class<?> entityClass;
    protected Iterator<String> paramNameIterator;

    protected AbstractRenderingContext(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * 获取单个值占位符
     *
     * @param column
     * @return
     */
    public abstract String getSingleValuePlaceholder(String column);

    /**
     * 获取两个值占位符
     *
     * @param column
     * @return
     */
    public abstract String getTwoValuePlaceholder(String column);

    /**
     * 获取一批占位符
     *
     * @param column
     * @return
     */
    public abstract String getListValuePlaceholder(String column);


    public void setParamNameIterator(Iterator<String> paramNameIterator) {
        this.paramNameIterator = paramNameIterator;
    }

    /**
     * 获取属性查询条件SQL表达式
     *
     * @param criterion
     * @return
     */
    public String getCriterionExpression(ColumnCriterion<?> criterion) {
        String placeholder = SQLConstants.EMPTY;
        if (criterion.isSingleValue()) {
            placeholder = getSingleValuePlaceholder(criterion.getColumn());
        } else if (criterion.isTwoValue()) {
            placeholder = getTwoValuePlaceholder(criterion.getColumn());
        } else if (criterion.isListValue()) {
            placeholder = getListValuePlaceholder(criterion.getColumn());
        }
        return criterion.patternSql(placeholder);
    }

    public String getPlaceholder(String column, String paramName, String prefix, String assignJdbcType, String assignTypeHandler) {
        if (StringUtils.isEmpty(assignJdbcType) || StringUtils.isEmpty(assignTypeHandler)) {
            RelationalProperty property = EntityMetadataRegistry.getInstance().getPropertyByColumn(entityClass, column);
            if (StringUtils.isEmpty(assignJdbcType) && property != null) {
                assignJdbcType = SQLPart.assignJdbcType(property.getJdbcType());
            }
            if (StringUtils.isEmpty(assignTypeHandler) && property != null) {
                assignTypeHandler = SQLPart.assignTypeHandler(property.getTypeHandler());
            }
        }
        return SQLPart.placeholder(paramName, prefix, assignJdbcType, assignTypeHandler);
    }
}
