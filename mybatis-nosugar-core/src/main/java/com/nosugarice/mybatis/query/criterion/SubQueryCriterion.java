package com.nosugarice.mybatis.query.criterion;

import com.nosugarice.mybatis.query.criteria.AbstractCriteriaQuery;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/12
 */
public class SubQueryCriterion extends AbstractCriterion<SubQueryCriterion> {

    private static final long serialVersionUID = 4537387464913122227L;

    private AbstractCriteriaQuery<?, ?> query;

    @Override
    public String getSql() {
        return null;
    }
}
