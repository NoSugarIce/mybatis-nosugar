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

import com.nosugarice.mybatis.sql.RenderingContext;

import java.util.function.BiPredicate;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public abstract class TwoValueCriterion<T, E extends TwoValueCriterion<T, E>> extends AbstractPropertyCriterion<T, E> {

    private static final long serialVersionUID = 3407529238253654774L;

    private T secondValue;

    public TwoValueCriterion(String column, String operator, T low, T high) {
        this(column, operator, low, high, Separator.AND);
    }

    public TwoValueCriterion(String column, String operator, T low, T high, Separator separator) {
        super(column, operator, low, separator);
        this.secondValue = high;
    }

    @Override
    public String getSql() {
        return SQL_FUNCTION.apply("? ?");
    }

    @Override
    public boolean isTwoValue() {
        return true;
    }

    @Override
    public T getSecondValue() {
        return secondValue;
    }

    public void setSecondValue(T secondValue) {
        this.secondValue = secondValue;
    }

    @Override
    public String renderPlaceholder(RenderingContext renderingContext) {
        return SQL_FUNCTION.apply(renderingContext.getTwoValuePlaceholder(column));
    }

    @SuppressWarnings("unchecked")
    public E when(BiPredicate<T, T> biPredicate) {
        setCondition(biPredicate.test(value, secondValue));
        return (E) this;
    }

}
