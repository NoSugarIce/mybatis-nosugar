package com.nosugarice.mybatis.test.logicdelete;

import com.nosugarice.mybatis.handler.ValueHandler;

/**
 * @author dingjingyang@foxmail.com
 * @date 2022/8/14
 */
public class LogicDeleteDisabledNameHandler implements ValueHandler<String> {

    private static final ThreadLocal<String> USER_NAME = new ThreadLocal<>();

    public static void setUserName(String userName) {
        USER_NAME.set(userName);
    }

    @Override
    public String setValue(String value) {
        return USER_NAME.get();
    }


}
