/*
 * Copyright 2021-2023 NoSugarIce
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nosugarice.mybatis.criteria.clause;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/12/11
 */
public interface UpdateSet<C, X extends UpdateSet<C, X>> {

    /**
     * 设置值
     *
     * @param column 列
     * @param value  值
     * @return
     */
    X set(C column, Object value);

    /**
     * 设置值
     *
     * @param column 列
     * @param value  值
     * @return
     */
    X setByColumn(String column, Object value);

    /**
     * 清空值
     *
     * @return
     */
    X cleanValues();

}
