package com.nosugarice.mybatis.sql;

import com.nosugarice.mybatis.mapper.function.FunS;
import com.nosugarice.mybatis.sql.ParameterBind.ParameterColumnBind;
import org.apache.ibatis.mapping.BoundSql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/24
 */
public class SqlAndParameterBind {

    private String sql;

    private final ParameterBind parameterBind;

    private FunS.Param3<Object, List<ParameterColumnBind>, BoundSql, Void> parameterHandle;

    private Map<String, Object> parameters;

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

    public SqlAndParameterBind(SqlAndParameterBind sqlAndParameterBind) {
        this.sql = sqlAndParameterBind.getSql();
        this.parameterBind = sqlAndParameterBind.getParameterBind();
        this.parameterHandle = sqlAndParameterBind.getParameterHandle();
        this.parameters = sqlAndParameterBind.getParameters();
    }

    public ParameterColumnBind bind(Object value, String column, Class<?> entityClass) {
        return parameterBind.bindValue(value, column, entityClass);
    }

    public void addParameter(String key, Object value) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put(key, value);
    }

    public String getSql() {
        return sql;
    }

    public SqlAndParameterBind setSql(String sql) {
        this.sql = sql;
        return this;
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

    public Map<String, Object> getParameters() {
        return parameters;
    }
}
