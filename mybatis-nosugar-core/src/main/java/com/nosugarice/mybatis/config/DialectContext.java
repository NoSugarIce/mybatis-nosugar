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

package com.nosugarice.mybatis.config;

import com.nosugarice.mybatis.dialect.Dialect;

/**
 * @author dingjingyang@foxmail.com
 * @date 2022/7/10
 */
public class DialectContext {

    static Dialect defaultDialect;

    private static final ThreadLocal<Dialect> DIALECT_THREAD_LOCAL = new ThreadLocal<>();

    public static Dialect getDefaultDialect() {
        return defaultDialect;
    }

    public static boolean isDominate() {
        return DIALECT_THREAD_LOCAL.get() != null;
    }

    public static void setDialect(Dialect dialect) {
        DIALECT_THREAD_LOCAL.set(dialect);
    }

    public static Dialect getDialect() {
        Dialect dialect = DIALECT_THREAD_LOCAL.get();
        if (dialect == null) {
            return defaultDialect;
        }
        return dialect;
    }

    public static void cleanDialect() {
        DIALECT_THREAD_LOCAL.remove();
    }

}
