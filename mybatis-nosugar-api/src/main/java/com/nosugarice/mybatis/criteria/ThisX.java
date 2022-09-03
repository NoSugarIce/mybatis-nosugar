package com.nosugarice.mybatis.criteria;

/**
 * @author dingjingyang@foxmail.com
 * @date 2022/8/20
 */
public interface ThisX<X extends ThisX<X>> {

    void setThis(X x);

    X getThis();

}
