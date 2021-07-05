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

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class GreaterThan<T> extends Comparison<T, GreaterThan<T>> {

    private static final long serialVersionUID = -6320179387110500588L;

    private static final ComparisonOperator OPERATOR = ComparisonOperator.GREATER_THAN;

    public GreaterThan(String column) {
        this(column, null);
    }

    public GreaterThan(String column, T value) {
        this(column, value, Separator.AND);
    }

    public GreaterThan(String column, T value, Separator separator) {
        super(column, value, OPERATOR, separator);
    }

}
