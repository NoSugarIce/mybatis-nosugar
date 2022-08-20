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

package com.nosugarice.mybatis.criteria.criterion;

import com.nosugarice.mybatis.criteria.Query;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/12/11
 */
public interface JoinCriterion<T1, C1, C, X extends JoinCriterion<T1, C1, C, X>> extends Query<T1, C1, X> {

    /**
     * 获取连接类型
     *
     * @return
     */
    JoinType getJoinType();

    /**
     * 获取表名
     *
     * @return
     */
    String getTable();

    /**
     * 获取表别名
     *
     * @return
     */
    String getTableAlias();

    /**
     * 连接属性
     *
     * @return
     */
    String getColumn();

    /**
     * 连接主表属性
     *
     * @return
     */
    String getMasterColumn();

    enum JoinType {

        INNER("INNER JOIN"),

        LEFT("LEFT JOIN"),

        RIGHT("RIGHT JOIN");

        private final String name;

        JoinType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

}
