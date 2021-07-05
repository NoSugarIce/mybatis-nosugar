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

import com.nosugarice.mybatis.query.criterion.GroupCriterionImpl;
import com.nosugarice.mybatis.sql.criterion.GroupCriterion;
import com.nosugarice.mybatis.sql.criterion.PropertyCriterion;

/**
 * @author dingjingyang@foxmail.com(dingjingyang)
 * @date 2020/12/19
 */
public interface Where<T, C> {

    /**
     * 添加条件
     *
     * @param test       是否添加到条件列表
     * @param criterions
     * @return
     */
    CriteriaQuery<T, C> addCriterion(boolean test, PropertyCriterion<?>... criterions);

    /**
     * 添加组条件
     *
     * @param groupCriterions
     * @return
     */
    CriteriaQuery<T, C> addGroupCriterion(GroupCriterion... groupCriterions);

    /**
     * 添加条件
     *
     * @param criterions
     * @return
     */
    default CriteriaQuery<T, C> addCriterion(PropertyCriterion<?>... criterions) {
        return addCriterion(true, criterions);
    }

    /**
     * 添加条件
     *
     * @param criterions
     * @return
     */
    default CriteriaQuery<T, C> and(PropertyCriterion<?>... criterions) {
        return addCriterion(criterions);
    }

    /**
     * 或条件
     * 参数列表的条件会被组装成or(...)
     *
     * @param criterions
     * @return
     */
    default CriteriaQuery<T, C> or(PropertyCriterion<?>... criterions) {
        return addGroupCriterion(new GroupCriterionImpl(criterions));
    }

}
