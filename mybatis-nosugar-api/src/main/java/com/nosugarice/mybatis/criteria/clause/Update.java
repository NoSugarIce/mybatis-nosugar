package com.nosugarice.mybatis.criteria.clause;

/**
 * @author dingjingyang@foxmail.com
 * @date 2022/8/20
 */
public interface Update<T, C, X extends Update<T, C, X>> extends UpdateSet<C, X>, From<T>, Where<C, X> {
}
