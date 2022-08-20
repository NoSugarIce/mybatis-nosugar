package com.nosugarice.mybatis.dialect;

import org.apache.ibatis.mapping.MappedStatement;

import javax.sql.DataSource;

/**
 * @author dingjingyang@foxmail.com
 * @date 2022/7/10
 */
public interface DialectFactory {

    Dialect getDialect(DataSource dataSource);

    Dialect getDialect(MappedStatement mappedStatement);

}
