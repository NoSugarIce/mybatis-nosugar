package com.nosugarice.mybatis.config;

import org.apache.ibatis.mapping.SqlCommandType;

/**
 * @author dingjingyang@foxmail.com
 * @date 2022/8/15
 */
public enum DmlType {

    SELECT(SqlCommandType.SELECT),
    INSERT(SqlCommandType.INSERT),
    UPDATE(SqlCommandType.UPDATE),
    DELETE(SqlCommandType.DELETE),
    LOGIC_DELETE(SqlCommandType.UPDATE);

    private final SqlCommandType sqlCommandType;

    DmlType(SqlCommandType sqlCommandType) {
        this.sqlCommandType = sqlCommandType;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }
}
