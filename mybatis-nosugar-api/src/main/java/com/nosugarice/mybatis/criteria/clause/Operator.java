package com.nosugarice.mybatis.criteria.clause;

/**
 * 用于值操作
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public interface Operator {

    /**
     * 否定的类型
     *
     * @return
     */
    Operator negated();

    /**
     * 操作符
     *
     * @return
     */
    String operator();
}
