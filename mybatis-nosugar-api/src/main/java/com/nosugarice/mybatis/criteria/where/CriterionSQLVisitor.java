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

package com.nosugarice.mybatis.criteria.where;

import com.nosugarice.mybatis.sql.SQLStrategy;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public interface CriterionSQLVisitor<S> {

    /**
     * 访问列条件
     *
     * @param criterion 属性条件
     * @return
     */
    S visit(ColumnCriterion<?> criterion);


    /**
     * 访问一组条件
     *
     * @param criterion 组条件
     * @return
     */
    S visit(GroupCriterion criterion);

    /**
     * 访问结果转换
     *
     * @param result
     * @return
     */
    String visitResultHandle(S result);

    /**
     * 为一组ColumnCriterion设置sql生成器
     *
     * @param groupCriterion
     * @param visitor
     */
    default void setSQLStrategy(GroupCriterion groupCriterion, CriterionSQLVisitor<S> visitor) {
        for (Criterion criterion : groupCriterion.getCriterions()) {
            if (criterion instanceof GroupCriterion) {
                setSQLStrategy((GroupCriterion) criterion, visitor);
            } else if (criterion instanceof ColumnCriterion) {
                criterion.setSqlStrategy((SQLStrategy) () -> visitResultHandle(criterion.accept(visitor)));
            }
        }
    }

}
