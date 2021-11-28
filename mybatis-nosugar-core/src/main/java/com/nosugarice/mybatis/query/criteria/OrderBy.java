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

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public interface OrderBy<C, X extends OrderBy<C, X>> extends ConvertToColumn<C> {

    /**
     * 排序
     *
     * @param column    列
     * @param ascending 是否正序
     * @return
     */
    X orderBy(C column, boolean ascending);

    /**
     * 正序排列
     *
     * @param column
     * @return
     */
    default X orderBy(C column) {
        return orderByAsc(column);
    }

    /**
     * 正序排列
     *
     * @param column
     * @return
     */
    default X orderByAsc(C column) {
        return orderBy(column, true);
    }

    /**
     * 倒叙排序
     *
     * @param column 列
     * @return
     */
    default X orderByDesc(C column) {
        return orderBy(column, false);
    }

}
