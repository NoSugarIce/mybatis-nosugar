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

/**
 * 属性查询条件
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public interface ColumnCriterion<T> extends Criterion {

    @Override
    default <S> S accept(CriterionSQLVisitor<S> visitor) {
        return visitor.visit(this);
    }

    /**
     * 否定
     *
     * @return
     */
    ColumnCriterion<T> not();

    /**
     * 获取列名
     *
     * @return
     */
    String getColumn();

    /**
     * 获取值
     *
     * @return
     */
    T getValue();

    /**
     * 获取第二个值
     *
     * @return
     */
    T getSecondValue();

    /**
     * 值是否没有
     *
     * @return
     */
    boolean isNoValue();

    /**
     * 值是否有一个
     *
     * @return
     */
    boolean isSingleValue();

    /**
     * 值是否有两个
     *
     * @return
     */
    boolean isTwoValue();

    /**
     * 值是否是集合
     *
     * @return
     */
    boolean isListValue();

    /**
     * @param value
     * @return
     */
    String patternSql(String value);

    /**
     * 用于值操作
     */
    interface Operator {

        /**
         * 否定的类型
         *
         * @return
         */
        Operator negated();

        /**
         * 操作符
         *
         * @return
         */
        String operator();
    }

}
