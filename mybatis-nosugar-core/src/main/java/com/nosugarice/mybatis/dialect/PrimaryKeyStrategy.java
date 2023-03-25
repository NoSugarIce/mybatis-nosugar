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

package com.nosugarice.mybatis.dialect;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/7
 */
public interface PrimaryKeyStrategy {

    /**
     * 是否支持自增
     *
     * @return
     */
    boolean supportsAutoIncrement();

    /**
     * 是否支持SelectId 方式
     *
     * @return
     */
    boolean supportsSelectIdentity();

    /**
     * selectId 语句
     *
     * @return
     */
    String getIdentitySelectString();

    /**
     * 在运行之前之前SelectId
     *
     * @return
     */
    boolean executeBeforeIdentitySelect();

}
