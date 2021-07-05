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
 * @date 2021/6/12
 */
public class KeyValue<T extends Serializable> extends SimpleValue<T> {

    private static final long serialVersionUID = -4609581874619363862L;

    /** 自增 */
    private boolean autoIncrement;

    /** id通过数据库查询获取时候的执行sql 或 IdGenerator 注册名称 */
    private String generator;

    @Override
    public boolean isInsertable() {
        return !autoIncrement;
    }

    @Override
    public boolean isUpdateable() {
        return false;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

}
