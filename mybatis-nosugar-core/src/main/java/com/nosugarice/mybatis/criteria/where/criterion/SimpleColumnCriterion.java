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

package com.nosugarice.mybatis.criteria.where.criterion;

import com.nosugarice.mybatis.criteria.ColumnReader;
import com.nosugarice.mybatis.criteria.Query;
import com.nosugarice.mybatis.criteria.criterion.ColumnCriterion;
import com.nosugarice.mybatis.sql.SQLPart;

import java.util.Collection;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class SimpleColumnCriterion<T, E extends SimpleColumnCriterion<T, E>> extends AbstractCriterion<E>
        implements ColumnReader, ColumnCriterion<T> {

    private static final long serialVersionUID = -459503157484510248L;

    protected String column;

    protected T value;

    protected T secondValue;

    protected OperatorType operatorType;

    protected boolean not;

    public SimpleColumnCriterion(String column, T value, OperatorType operatorType) {
        this.column = column;
        this.value = value;
        this.operatorType = operatorType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E not() {
        this.not = !not;
        return (E) this;
    }

    @Override
    public String patternSql(String value) {
        return SQLPart.merge(getConnector(), withTableAliasPlaceholder(), (not ? operatorType.negated() : operatorType).operator(), value);
    }

    /**
     * 获取比较操作符
     *
     * @return
     */
    public OperatorType getOperator() {
        return operatorType;
    }

    public void setOperatorType(OperatorType operatorType) {
        this.operatorType = operatorType;
    }

    @Override
    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    @Override
    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public T getSecondValue() {
        return secondValue;
    }

    public void setSecondValue(T secondValue) {
        this.secondValue = secondValue;
    }

    @Override
    public boolean isNoValue() {
        return value == null;
    }

    @Override
    public boolean isSingleValue() {
        return !isNoValue() && !isTwoValue() && !isListValue() && !isQueryValue();
    }

    @Override
    public boolean isTwoValue() {
        return secondValue != null;
    }

    @Override
    public boolean isListValue() {
        return value != null && value instanceof Collection;
    }

    @Override
    public boolean isQueryValue() {
        return value != null && value instanceof Query;
    }

}
