package com.nosugarice.mybatis.test.base.valuehandler;

import com.nosugarice.mybatis.handler.ValueHandler;

import java.time.LocalDateTime;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/11/26
 */
public class NowHandler implements ValueHandler<LocalDateTime> {
    @Override
    public LocalDateTime setValue(LocalDateTime value) {
        return LocalDateTime.now();
    }
}
