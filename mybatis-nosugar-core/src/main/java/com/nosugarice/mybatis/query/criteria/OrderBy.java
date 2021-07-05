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

import com.nosugarice.mybatis.query.process.Order;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public interface OrderBy<T, C> extends ConvertToColumn<C> {

    /**
     * 正序排列
     *
     * @param order
     * @return
     */
    CriteriaQuery<T, C> orderBy(Order order);

    /**
     * 正序排列
     *
     * @param column
     * @return
     */
    default CriteriaQuery<T, C> orderBy(C column) {
        return orderByAsc(column);
    }

    /**
     * 正序排列
     *
     * @param column
     * @return
     */
    default CriteriaQuery<T, C> orderByAsc(C column) {
        return orderBy(true, column);
    }

    /**
     * 倒叙排序
     *
     * @param column 列
     * @return
     */
    default CriteriaQuery<T, C> orderByDesc(C column) {
        return orderBy(false, column);
    }

    /**
     * 排序
     *
     * @param ascending 是否正序
     * @param column    列
     * @return
     */
    default CriteriaQuery<T, C> orderBy(boolean ascending, C column) {
        return orderBy(new Order(ascending, toColumn(column)));
    }

}
