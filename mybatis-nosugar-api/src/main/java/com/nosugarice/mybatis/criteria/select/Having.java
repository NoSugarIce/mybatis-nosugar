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

package com.nosugarice.mybatis.criteria.select;

import com.nosugarice.mybatis.criteria.where.ColumnCriterion.Operator;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public interface Having<C, X extends Having<C, X>> {

    /**
     * 筛选分组后数据
     *
     * @param function 函数
     * @param column   列
     * @param operator 操作类型
     * @param value    值
     * @param <V>      值类型
     * @return
     */
    <V> X having(AggFunction function, C column, Operator operator, V value);

}
