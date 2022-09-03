package com.nosugarice.mybatis.criteria.delete;

import com.nosugarice.mybatis.criteria.clause.Delete;
import com.nosugarice.mybatis.criteria.tocolumn.ToColumn;
import com.nosugarice.mybatis.criteria.where.AbstractWhere;

/**
 * @author dingjingyang@foxmail.com
 * @date 2022/8/20
 */
public abstract class AbstractDelete<T, C, X extends Delete<T, C, X>> extends AbstractWhere<C, X> implements Delete<T, C, X> {

    public AbstractDelete(Class<T> entityClass, ToColumn<C> toColumn) {
        super(entityClass, toColumn);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getType() {
        return (Class<T>) getEntityClass();
    }

}
