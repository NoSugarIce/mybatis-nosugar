package com.nosugarice.mybatis.dialect;

import com.nosugarice.mybatis.config.DialectContext;

/**
 * @author dingjingyang@foxmail.com
 * @date 2022/7/10
 */
public class RuntimeDialect implements Dialect {

    @Override
    public Identity getIdentity() {
        return DialectContext.getDialect().getIdentity();
    }

    @Override
    public Limitable getLimitHandler() {
        return DialectContext.getDialect().getLimitHandler();
    }

}
