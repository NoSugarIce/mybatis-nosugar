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

package com.nosugarice.mybatis.mapping.value;

import java.io.Serializable;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/6
 */
public interface Value<T extends Serializable> extends Serializable {

    /**
     * 可插入
     *
     * @return
     */
    boolean isInsertable();

    /**
     * 可更新
     *
     * @return
     */
    boolean isUpdateable();


    /**
     * 获取默认值
     * 默认值可以在程序初始化时候确认,直接拼接到sql了
     *
     * @return
     */
    T getDefaultValue();

}
