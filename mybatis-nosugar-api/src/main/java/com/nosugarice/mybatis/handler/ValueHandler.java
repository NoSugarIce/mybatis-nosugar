package com.nosugarice.mybatis.handler;

/**
 * 值处理器,实现需注意线程安全问题
 *
 * @author dingjingyang@foxmail.com
 * @date 2021/11/22
 */
public interface ValueHandler<T> {

    /**
     * 设置值
     *
     * @param value
     * @return
     */
    T setValue(T value);

}
