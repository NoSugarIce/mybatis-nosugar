package com.nosugarice.mybatis.sqlsource;

import com.nosugarice.mybatis.sql.SqlAndParameterBind;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.session.Configuration;

/**
 * @author dingjingyang@foxmail.com
 * @date 2023/4/1
 */
public class ParameterHandleRawSqlSource extends RawSqlSource {

    private final SqlAndParameterBind sqlAndParameterBind;

    public ParameterHandleRawSqlSource(Configuration configuration, SqlAndParameterBind sqlAndParameterBind, Class<?> parameterType) {
        super(configuration, sqlAndParameterBind.getSql(), parameterType);
        this.sqlAndParameterBind = sqlAndParameterBind;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        BoundSql boundSql = super.getBoundSql(parameterObject);
        if (sqlAndParameterBind.hasParameterHandle()) {
            sqlAndParameterBind.getParameterHandles().forEach(handle -> handle.apply(
                    parameterObject, sqlAndParameterBind.getParameterBind().getParameterColumnBinds(), boundSql));
        }
        return boundSql;
    }


}
