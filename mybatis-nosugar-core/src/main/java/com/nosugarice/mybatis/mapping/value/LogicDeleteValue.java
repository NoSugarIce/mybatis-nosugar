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
 * 逻辑删除的初始值和删除值是确定的,直接拼接到sql
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/12/6
 */
public class LogicDeleteValue extends SimpleValue<Serializable> {

    private static final long serialVersionUID = 3607357378934254038L;

    /** 默认值 */
    private final Serializable defaultValue;

    /** 逻辑删除值 */
    private final Serializable logicDeleteValue;

    public LogicDeleteValue(Serializable defaultValue, Serializable logicDeleteValue) {
        this.defaultValue = defaultValue;
        this.logicDeleteValue = logicDeleteValue;
    }

    @Override
    public boolean isUpdateable() {
        return false;
    }

    @Override
    public Serializable getDefaultValue() {
        return defaultValue;
    }

    public Serializable getLogicDeleteValue() {
        return logicDeleteValue;
    }

}
