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
