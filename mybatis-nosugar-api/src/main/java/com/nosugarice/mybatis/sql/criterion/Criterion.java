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

package com.nosugarice.mybatis.sql.criterion;

import com.nosugarice.mybatis.sql.Expression;

/**
 * 查询条件
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public interface Criterion extends Expression<Criterion> {

    /**
     * 是否添加到条件列表
     *
     * @return
     */
    boolean condition();

    /** 条件直接的连接符 */
    enum Separator {
        AND,
        OR
    }

    /**
     * 获取连接符
     *
     * @return 连接符
     */
    Separator getSeparator();

    /**
     * 设置连接符
     *
     * @param separator 连接符
     */
    void setSeparator(Separator separator);

}
