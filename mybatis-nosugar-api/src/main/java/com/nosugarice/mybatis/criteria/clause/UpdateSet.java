package com.nosugarice.mybatis.criteria.clause;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/12/11
 */
public interface UpdateSet<C, X extends UpdateSet<C, X>> {

    /**
     * 设置值
     *
     * @param column 列
     * @param value  值
     * @return
     */
    X set(C column, Object value);

    /**
     * 设置值
     *
     * @param column 列
     * @param value  值
     * @return
     */
    X setByColumn(String column, Object value);

    /**
     * 清空值
     *
     * @return
     */
    X cleanValues();

}
