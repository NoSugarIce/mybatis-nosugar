package com.nosugarice.mybatis.annotation;

import com.nosugarice.mybatis.handler.ValueHandler;

import java.io.Serializable;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当进行插入时的赋值操作
 *
 * @author dingjingyang@foxmail.com
 * @date 2023/4/9
 */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InsertHandler {

    Class<? extends ValueHandler<? extends Serializable>> value();

}
