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

package com.nosugarice.mybatis.assign.value;

import com.nosugarice.mybatis.handler.ValueHandler;

import java.io.Serializable;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/6
 */
public interface Value {

    Value SIMPLE_VALUE = new SimpleValue();

    Value SIMPLE_KEY_VALUE = new KeyValue();

    /**
     * 值类型
     *
     * @return
     */
    Class<?> getType();

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
     * 可软删
     *
     * @return
     */
    boolean isLogicDelete();

    /**
     * 插入处理
     *
     * @return
     */
    ValueHandler<?> insertHandler();

    /**
     * 更新处理
     *
     * @return
     */
    ValueHandler<?> updateHandler();

    /**
     * 软删除处理
     *
     * @return
     */
    ValueHandler<?> logicDeleteHandler();

    /**
     * 返回处理
     *
     * @return
     */
    ValueHandler<?> resultHandler();

    /**
     * 条件处理
     *
     * @return
     */
    ValueHandler<?> conditionHandler();


    /**
     * 填充处理器
     *
     * @return
     */
    ValueHandler<?> fillHandler();

    boolean isInsertFill();

    boolean isUpdateFill();

    boolean isConditionFill();

    /**
     * 获取默认值
     * 默认值可以在程序初始化时候确认,直接拼接到sql了
     *
     * @return
     */
    default Serializable getDefaultValue() {
        return null;
    }

}
