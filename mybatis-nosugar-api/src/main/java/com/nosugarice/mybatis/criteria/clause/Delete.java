package com.nosugarice.mybatis.criteria.clause;

/**
 * @author dingjingyang@foxmail.com
 * @date 2022/8/20
 */
public interface Delete<T, C, X extends Delete<T, C, X>> extends From<T>, Where<C, X> {
}
