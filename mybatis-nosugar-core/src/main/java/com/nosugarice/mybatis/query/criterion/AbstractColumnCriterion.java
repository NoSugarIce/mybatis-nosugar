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

package com.nosugarice.mybatis.query.criterion;

import com.nosugarice.mybatis.query.criteria.ColumnReader;

import java.util.function.Function;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public abstract class AbstractColumnCriterion<T, E extends AbstractColumnCriterion<T, E>> extends AbstractCriterion<E>
        implements ColumnReader, ColumnCriterion<T> {

    private static final long serialVersionUID = -459503157484510248L;

    protected final Function<String, String> SQL_FUNCTION = value ->
            append(getSeparator().name(), withTableAliasPlaceholder(), getOperator(isNegated()), value).merge();

    protected String column;

    protected String operator;

    protected T value;

    protected boolean negated;

    protected AbstractColumnCriterion(String column, String operator) {
        this.column = column;
        this.operator = operator;
    }

    protected AbstractColumnCriterion(String column, String operator, Separator separator) {
        this.column = column;
        this.operator = operator;
        this.separator = separator;
    }

    public AbstractColumnCriterion(String column, String operator, T value) {
        this.column = column;
        this.operator = operator;
        this.value = value;
    }

    public AbstractColumnCriterion(String column, String operator, T value, Separator separator) {
        this.column = column;
        this.operator = operator;
        this.value = value;
        this.separator = separator;
    }

    @Override
    public <S> S accept(ColumnCriterionVisitor<S> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean isNegated() {
        return negated;
    }

    @Override
    public String getOperator(boolean negated) {
        return negated ? "NOT " + getOperator() : getOperator();
    }

    @Override
    public String getSql() {
        return SQL_FUNCTION.apply("?");
    }

    @Override
    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
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
        return null;
    }

    public void setNegated(boolean negated) {
        this.negated = negated;
    }

    @Override
    public boolean isNoValue() {
        return false;
    }

    @Override
    public boolean isSingleValue() {
        return false;
    }

    @Override
    public boolean isTwoValue() {
        return false;
    }

    @Override
    public boolean isListValue() {
        return false;
    }

}
