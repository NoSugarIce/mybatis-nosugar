package com.nosugarice.mybatis.sql;

import com.nosugarice.mybatis.mapper.function.FunS;
import com.nosugarice.mybatis.sql.ParameterBind.ParameterColumnBind;
import org.apache.ibatis.mapping.BoundSql;

import java.util.List;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/24
 */
public class SqlAndParameterBind {

    private String sql;

    private final ParameterBind parameterBind;

    private FunS.Param3<Object, List<ParameterColumnBind>, BoundSql, Void> parameterHandle;

    public SqlAndParameterBind() {
        parameterBind = new ParameterBind();
    }

    public SqlAndParameterBind(String sql) {
        this.sql = sql;
        parameterBind = new ParameterBind();
    }

    public SqlAndParameterBind(ParameterBind parameterBind) {
        this.parameterBind = parameterBind;
    }

    public SqlAndParameterBind(String sql, ParameterBind parameterBind) {
        this.sql = sql;
        this.parameterBind = parameterBind;
    }

    public ParameterColumnBind bind(Object value, String column, Class<?> entityClass) {
        return parameterBind.bindValue(value, column, entityClass);
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public ParameterBind getParameterBind() {
        return parameterBind;
    }

    public FunS.Param3<Object, List<ParameterColumnBind>, BoundSql, Void> getParameterHandle() {
        return parameterHandle;
    }

    public void setParameterHandle(FunS.Param3<Object, List<ParameterColumnBind>, BoundSql, Void> parameterHandle) {
        this.parameterHandle = parameterHandle;
    }
}
