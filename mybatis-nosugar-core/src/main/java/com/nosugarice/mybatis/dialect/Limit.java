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

package com.nosugarice.mybatis.dialect;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public interface Limit {

    /**
     * 是否支持分页
     *
     * @return
     */
    boolean supportsLimit();

    /**
     * 处理sql 添加分页方法
     *
     * @param sql
     * @param offset
     * @param limit
     * @return
     */
    String processSql(String sql, int offset, int limit);

    /**
     * 是否不从头开始
     *
     * @param offset
     * @return
     */
    default boolean hasFirstRow(int offset) {
        return offset > 0;
    }

}
