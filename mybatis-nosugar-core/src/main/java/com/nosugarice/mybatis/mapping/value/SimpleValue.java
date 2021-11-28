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

import com.nosugarice.mybatis.handler.ValueHandler;

import java.io.Serializable;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/6/12
 */
public class SimpleValue implements Value {

    private Class<?> type;

    private ValueHandler<?> insertHandler;

    private ValueHandler<?> updateHandler;

    private ValueHandler<?> resultHandler;

    public SimpleValue() {
    }

    public SimpleValue(Class<?> type) {
        this.type = type;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public boolean isInsertable() {
        return true;
    }

    @Override
    public boolean isUpdateable() {
        return true;
    }

    @Override
    public ValueHandler<?> insertHandler() {
        return insertHandler;
    }

    public void setInsertHandler(ValueHandler<?> insertHandler) {
        this.insertHandler = insertHandler;
    }

    @Override
    public ValueHandler<?> updateHandler() {
        return updateHandler;
    }

    public void setUpdateHandler(ValueHandler<?> updateHandler) {
        this.updateHandler = updateHandler;
    }

    @Override
    public ValueHandler<?> resultHandler() {
        return resultHandler;
    }

    public void setResultHandler(ValueHandler<?> resultHandler) {
        this.resultHandler = resultHandler;
    }

    @Override
    public Serializable getDefaultValue() {
        return null;
    }
}
