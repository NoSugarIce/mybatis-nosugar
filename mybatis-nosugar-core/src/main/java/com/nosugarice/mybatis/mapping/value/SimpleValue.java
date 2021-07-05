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
 * @author dingjingyang@foxmail.com(dingjingyang)
 * @date 2021/6/12
 */
public class SimpleValue<T extends Serializable> implements Value<T> {

    private static final long serialVersionUID = 2903179613818694904L;

    @Override
    public boolean isInsertable() {
        return true;
    }

    @Override
    public boolean isUpdateable() {
        return true;
    }

    @Override
    public T getDefaultValue() {
        return null;
    }
}
