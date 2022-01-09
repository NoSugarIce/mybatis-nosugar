package com.nosugarice.mybatis.test.base.typehandler;

import com.nosugarice.mybatis.test.base.mode.StatusEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/1/17
 */
public class StatusEnumTypeHandler extends BaseTypeHandler<StatusEnum> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, StatusEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getStatus());
    }

    @Override
    public StatusEnum getNullableResult(ResultSet rs, String column) throws SQLException {
        Integer status = rs.getInt(column);
        return StatusEnum.valueOfStatus(status);
    }

    @Override
    public StatusEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Integer status = rs.getInt(columnIndex);
        return StatusEnum.valueOfStatus(status);
    }

    @Override
    public StatusEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Integer status = cs.getInt(columnIndex);
        return StatusEnum.valueOfStatus(status);
    }
}
