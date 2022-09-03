package com.nosugarice.mybatis.criteria.update;

import com.nosugarice.mybatis.criteria.clause.Update;
import com.nosugarice.mybatis.criteria.tocolumn.ToColumn;
import com.nosugarice.mybatis.criteria.where.AbstractWhere;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2022/8/20
 */
public abstract class AbstractUpdate<T, C, X extends Update<T, C, X>> extends AbstractWhere<C, X> implements Update<T, C, X>, UpdateStructure {
    private final Map<String, Object> values = new LinkedHashMap<>();

    public AbstractUpdate(Class<T> entityClass, ToColumn<C> toColumn) {
        super(entityClass, toColumn);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getType() {
        return (Class<T>) getEntityClass();
    }

    @Override
    public X set(C column, Object value) {
        values.put(toColumn(column), value);
        return getThis();
    }

    @Override
    public X setByColumn(String column, Object value) {
        values.put(column, value);
        return getThis();
    }

    @Override
    public X cleanValues() {
        values.clear();
        return getThis();
    }

    @Override
    public Map<String, Object> getSetValues() {
        return values;
    }

}
