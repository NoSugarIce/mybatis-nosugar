package com.nosugarice.mybatis.test.logicdelete.deleteValueHandler;

import com.nosugarice.mybatis.handler.ValueHandler;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author dingjingyang@foxmail.com
 * @date 2022/8/14
 */
public class DateValueHandler implements ValueHandler<Integer> {

    @Override
    public Integer setValue(Integer value) {
        return (int) LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
    }
}
