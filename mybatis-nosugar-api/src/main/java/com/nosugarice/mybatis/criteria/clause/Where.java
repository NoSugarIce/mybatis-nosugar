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

package com.nosugarice.mybatis.criteria.clause;

import com.nosugarice.mybatis.criteria.ThisX;
import com.nosugarice.mybatis.criteria.criterion.Criterion;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public interface Where<C, X extends Where<C, X>> extends PredicateCondition<C, X>, ThisX<X> {

    /**
     * 添加条件
     *
     * @param criterions
     * @return
     */
    default X and(Criterion... criterions) {
        return addCriterion(criterions);
    }

    /**
     * 或条件
     * 参数列表的条件会被组装成or(...)
     *
     * @param criterions
     * @return
     */
    X or(Criterion... criterions);

    /**
     * 包含被软删除的数据
     *
     * @return
     */
    X includeLogicDelete();

}
