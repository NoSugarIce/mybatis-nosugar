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

package com.nosugarice.mybatis.query.criteria;

import com.nosugarice.mybatis.query.criteria.function.FunctionSelection;
import com.nosugarice.mybatis.query.criteria.function.agg.AggFunction;
import com.nosugarice.mybatis.query.criteria.function.agg.Aggregation;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public interface Select<C, T, X extends Select<C, T, X>> extends From<T>, ConvertToColumn<C> {

    /**
     * 去重
     *
     * @return
     */
    X distinct();

    /**
     * 是否去重
     *
     * @return
     */
    boolean isDistinct();

    /**
     * 查询字段
     *
     * @param columns
     * @return
     */
    X select(C... columns);

    /**
     * 排除字段
     *
     * @param columns
     * @return
     */
    X exclude(C... columns);

    /**
     * 聚合字段
     *
     * @param functionSelection
     * @return
     */
    X expand(FunctionSelection functionSelection);


    /**
     * 聚合函数字段
     *
     * @param aggregation
     * @param column
     * @param alias
     * @return
     */
    default X expand(Aggregation aggregation, C column, String alias) {
        AggFunction function = new AggFunction(aggregation, toColumn(column, getFromType()));
        function.alias(alias);
        return expand(function);
    }

}