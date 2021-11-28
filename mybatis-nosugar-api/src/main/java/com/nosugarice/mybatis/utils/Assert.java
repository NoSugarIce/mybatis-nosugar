package com.nosugarice.mybatis.utils;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/11/6
 */
public class Assert {

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

}
