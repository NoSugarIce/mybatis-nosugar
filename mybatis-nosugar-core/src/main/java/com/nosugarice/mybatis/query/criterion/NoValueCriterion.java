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

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public abstract class NoValueCriterion<T, E extends NoValueCriterion<T, E>> extends AbstractColumnCriterion<T, E> {

    private static final long serialVersionUID = -8957654596073038239L;

    protected NoValueCriterion(String column, String operator) {
        super(column, operator);
    }

    protected NoValueCriterion(String column, String operator, Separator separator) {
        super(column, operator, separator);
    }

    @Override
    public String getSql() {
        return SQL_FUNCTION.apply("");
    }

    @Override
    public boolean isNoValue() {
        return true;
    }

    @Override
    public String renderPlaceholder(RenderingContext renderingContext) {
        return getSql();
    }

}
