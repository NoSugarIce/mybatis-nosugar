package com.nosugarice.mybatis.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/11/26
 */
public class ResultValueHandler<T> implements TypeHandler<T> {

    private final TypeHandler<T> typeHandler;
    private final ValueHandler<T> valueHandler;

    public ResultValueHandler(TypeHandler<T> typeHandler, ValueHandler<T> valueHandler) {
        this.typeHandler = typeHandler;
        this.valueHandler = valueHandler;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        typeHandler.setParameter(ps, i, parameter, jdbcType);
    }

    @Override
    public T getResult(ResultSet rs, String columnName) throws SQLException {
        return valueHandler.setValue(typeHandler.getResult(rs, columnName));
    }

    @Override
    public T getResult(ResultSet rs, int columnIndex) throws SQLException {
        return valueHandler.setValue(typeHandler.getResult(rs, columnIndex));
    }

    @Override
    public T getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return valueHandler.setValue(typeHandler.getResult(cs, columnIndex));
    }

}