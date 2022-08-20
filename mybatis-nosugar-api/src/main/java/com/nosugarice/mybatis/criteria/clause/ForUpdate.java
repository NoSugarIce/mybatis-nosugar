package com.nosugarice.mybatis.criteria.clause;

/**
 * @author dingjingyang@foxmail.com
 * @date 2022/6/19
 */
public interface ForUpdate<X extends ForUpdate<X>> {

    /**
     * 查询时锁表
     *
     * @return
     */
    X forUpdate();

}
