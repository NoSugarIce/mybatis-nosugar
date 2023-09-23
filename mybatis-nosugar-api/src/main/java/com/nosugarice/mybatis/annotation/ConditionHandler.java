package com.nosugarice.mybatis.annotation;

import com.nosugarice.mybatis.handler.ValueHandler;

import java.io.Serializable;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author dingjingyang@foxmail.com
 * @date 2023/4/9
 */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionHandler {

    Class<? extends ValueHandler<? extends Serializable>> value();

}
