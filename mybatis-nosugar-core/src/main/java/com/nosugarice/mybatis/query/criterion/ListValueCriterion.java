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
import com.nosugarice.mybatis.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public abstract class ListValueCriterion<T, E extends ListValueCriterion<T, E>> extends AbstractPropertyCriterion<T, E> {

    private static final long serialVersionUID = 1138903658585350512L;

    protected final Collection<T> values;

    public ListValueCriterion(String column, String operator, T[] values) {
        this(column, operator, values, Separator.AND);
    }

    public ListValueCriterion(String column, String operator, T[] values, Separator separator) {
        this(column, operator, Arrays.asList(values), separator);
    }

    public ListValueCriterion(String column, String operator, Collection<T> values) {
        this(column, operator, values, Separator.AND);
    }

    public ListValueCriterion(String column, String operator, Collection<T> values, Separator separator) {
        super(column, operator, separator);
        this.values = values;
    }

    @Override
    public String getSql() {
        return SQL_FUNCTION.apply("(" + StringUtils.join(value, ",") + ")");
    }

    @Override
    public boolean isListValue() {
        return true;
    }

    @Override
    public String renderPlaceholder(RenderingContext renderingContext) {
        return SQL_FUNCTION.apply(renderingContext.getListValuePlaceholder(column));
    }

    @SuppressWarnings("unchecked")
    public E when(Predicate<T> predicate) {
        setCondition(predicate.test(value));
        return (E) this;
    }

    public Collection<T> getValues() {
        return values;
    }
}
