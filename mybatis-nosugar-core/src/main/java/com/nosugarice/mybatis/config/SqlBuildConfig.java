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

package com.nosugarice.mybatis.config;

import com.nosugarice.mybatis.dialect.Dialect;
import com.nosugarice.mybatis.dialect.DialectFactory;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/6
 */
public class SqlBuildConfig {

    /** 忽略空字符 */
    private boolean ignoreEmptyChar;

    private DialectFactory dialectFactory;

    /** 数据库方言 */
    private Dialect dialect;

    /**
     * 自动判断数据库
     */
    private boolean runtimeDialect;

    public boolean isIgnoreEmptyChar() {
        return ignoreEmptyChar;
    }

    public void setIgnoreEmptyChar(boolean ignoreEmptyChar) {
        this.ignoreEmptyChar = ignoreEmptyChar;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public DialectFactory getDialectFactory() {
        return dialectFactory;
    }

    public void setDialectFactory(DialectFactory dialectFactory) {
        this.dialectFactory = dialectFactory;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    public boolean isRuntimeDialect() {
        return runtimeDialect;
    }

    public void setRuntimeDialect(boolean runtimeDialect) {
        this.runtimeDialect = runtimeDialect;
    }
}
