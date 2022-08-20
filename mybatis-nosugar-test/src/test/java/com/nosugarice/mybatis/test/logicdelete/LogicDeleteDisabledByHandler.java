package com.nosugarice.mybatis.test.logicdelete;

import com.nosugarice.mybatis.handler.ValueHandler;

/**
 * @author dingjingyang@foxmail.com
 * @date 2022/8/14
 */
public class LogicDeleteDisabledByHandler implements ValueHandler<Integer> {

    private static final ThreadLocal<Integer> USER_ID = new ThreadLocal<>();

    public static void setUserId(Integer userId) {
        USER_ID.set(userId);
    }

    @Override
    public Integer setValue(Integer value) {
        return USER_ID.get();
    }

}
