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

import com.nosugarice.mybatis.sql.SQLStrategy;

import java.util.function.Predicate;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public abstract class SingleValueCriterion<T, E extends SingleValueCriterion<T, E>> extends AbstractColumnCriterion<T, E> {

    private static final long serialVersionUID = 662382880058191739L;

    protected SingleValueCriterion(String column, T value) {
        super(column, value);
        setSqlStrategy((SQLStrategy) () -> patternSql("?"));
    }

    @Override
    public boolean isSingleValue() {
        return true;
    }

    @SuppressWarnings("unchecked")
    public E when(Predicate<T> predicate) {
        setCondition(predicate.test(value));
        return (E) this;
    }

}
