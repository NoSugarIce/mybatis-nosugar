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

import com.nosugarice.mybatis.criteria.select.JoinCriterion.JoinType;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public interface Join<C, X> {

    /**
     * 关联表
     *
     * @param joinCriterion 关联信息
     * @return
     */
    <T1, C1> X join(JoinCriterion<T1, C1, C, ?> joinCriterion);

    /**
     * 关联表
     *
     * @param joinType       关联类型
     * @param entityClass    关联表类型
     * @param property       关联表属性
     * @param masterProperty 主表属性
     * @return
     */
    <T1, C1> JoinCriterion<T1, C1, C, ?> buildJoin(JoinType joinType, Class<T1> entityClass, C1 property, C masterProperty);

    /**
     * 等值关联
     *
     * @param entityClass    关联表类型
     * @param property       关联表属性
     * @param masterProperty 主表属性
     * @return
     */
    default <T1, C1> JoinCriterion<T1, C1, C, ?> buildInnerJoin(Class<T1> entityClass, C1 property, C masterProperty) {
        return buildJoin(JoinType.INNER, entityClass, property, masterProperty);
    }

    /**
     * 左关联
     *
     * @param entityClass    关联表类型
     * @param property       关联表属性
     * @param masterProperty 主表属性
     * @return
     */
    default <T1, C1> JoinCriterion<T1, C1, C, ?> buildLeftJoin(Class<T1> entityClass, C1 property, C masterProperty) {
        return buildJoin(JoinType.LEFT, entityClass, property, masterProperty);
    }

    /**
     * 右关联
     *
     * @param entityClass    关联表类型
     * @param property       关联表属性
     * @param masterProperty 主表属性
     * @return
     */
    default <T1, C1> JoinCriterion<T1, C1, C, ?> buildRightJoin(Class<T1> entityClass, C1 property, C masterProperty) {
        return buildJoin(JoinType.RIGHT, entityClass, property, masterProperty);
    }

}
