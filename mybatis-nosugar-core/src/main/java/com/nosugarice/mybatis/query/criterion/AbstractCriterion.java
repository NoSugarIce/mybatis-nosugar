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

import com.nosugarice.mybatis.sql.SqlFragment;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public abstract class AbstractCriterion<T extends AbstractCriterion<T>> extends SqlFragment implements Criterion {

    private static final long serialVersionUID = 1L;

    private boolean condition = true;

    protected Separator separator;

    @Override
    public boolean condition() {
        return condition;
    }

    public void setCondition(boolean condition) {
        this.condition = condition;
    }

    @Override
    public Separator getSeparator() {
        return separator == null ? Separator.AND : separator;
    }

    @Override
    public void setSeparator(Separator separator) {
        this.separator = separator;
    }

    public T and() {
        setSeparator(Separator.AND);
        return getThis();
    }

    public T or() {
        setSeparator(Separator.OR);
        return getThis();
    }

    @SuppressWarnings("unchecked")
    protected T getThis() {
        return (T) this;
    }

}
