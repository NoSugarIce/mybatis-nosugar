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

import com.nosugarice.mybatis.sql.Expression;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public interface Selection extends Expression {

    /**
     * 设置别名
     *
     * @param alias
     * @return
     */
    <T extends Selection> T alias(String alias);

    /**
     * 获取别名
     *
     * @return
     */
    String getAlias();

    /**
     * 是否列查询
     *
     * @return
     */
    default boolean isColumnSelection() {
        return false;
    }

    /**
     * 是否方法查询
     *
     * @return
     */
    default boolean isFunctionSelection() {
        return false;
    }
}
