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
 * @date 2020/11/15
 */
public interface Dialect {

    /**
     * 处理数据库关键字
     *
     * @param name
     * @return
     */
    default String processKeywords(String name) {
        return "\"" + name + "\"";
    }

    /**
     * 获取主键策略
     *
     * @return
     */
    Identity getIdentity();

    /**
     * 获取分页处理方法
     *
     * @return
     */
    Limitable getLimitHandler();

    /**
     * 获取字面值处理器
     *
     * @return
     */
    default LiteralValueHandler getLiteralValueHandler() {
        return LiteralValueHandler.getLiteralValueHandler();
    }

}
