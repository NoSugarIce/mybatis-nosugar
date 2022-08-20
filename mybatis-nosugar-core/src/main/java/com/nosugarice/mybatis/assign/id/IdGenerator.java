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

package com.nosugarice.mybatis.assign.id;

import org.apache.ibatis.executor.Executor;

import java.io.Serializable;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/6/12
 */
public interface IdGenerator<T extends Serializable> {

    /**
     * 获取类型
     *
     * @return
     */
    Class<T> getType();

    /**
     * 生成主键值
     *
     * @param executor
     * @param parameter
     * @return
     */
    T generate(Executor executor, Object parameter);

}
