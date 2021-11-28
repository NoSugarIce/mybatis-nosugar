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

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/6
 */
public class SqlBuildConfig {

    /** 忽略空字符 */
    private boolean ignoreEmptyChar;

    /** 数据库方言 */
    private Dialect dialect;

    public boolean isIgnoreEmptyChar() {
        return ignoreEmptyChar;
    }

    public void setIgnoreEmptyChar(boolean ignoreEmptyChar) {
        this.ignoreEmptyChar = ignoreEmptyChar;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }
}
