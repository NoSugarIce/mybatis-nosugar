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


import java.util.Collection;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public interface Select<C, X extends Select<C, X>> {

    /**
     * 去重
     *
     * @return X
     */
    X distinct();

    /**
     * 查询特定字段
     *
     * @param columns 一组列
     * @return X
     */
    X select(Collection<C> columns);

    /**
     * 查询结果排除特定字段
     *
     * @param columns 一组列
     * @return X
     */
    X exclude(Collection<C> columns);

    /**
     * 聚合字段
     *
     * @param sqlFunction 函数
     * @param column      列
     * @param alias       别名
     * @return X
     */
    X expand(SQLFunction sqlFunction, C column, String alias);

}