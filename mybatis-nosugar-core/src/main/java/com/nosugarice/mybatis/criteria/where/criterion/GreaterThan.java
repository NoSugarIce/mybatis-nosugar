/*
 * Copyright 2021-2023 NoSugarIce
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nosugarice.mybatis.criteria.where.criterion;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class GreaterThan<T> extends SingleValueCriterion<T, GreaterThan<T>> {

    private static final long serialVersionUID = -6320179387110500588L;

    public GreaterThan(String column) {
        this(column, null);
    }

    public GreaterThan(String column, T value) {
        super(column, value, OperatorType.GREATER_THAN);
    }

}
