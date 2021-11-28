/*
 *    Copyright 2021 NoSugarIce
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nosugarice.mybatis.sqlsource;

import com.nosugarice.mybatis.builder.sql.SqlScriptBuilder;
import com.nosugarice.mybatis.config.MetadataBuildingContext;
import com.nosugarice.mybatis.mapper.function.FunS;
import com.nosugarice.mybatis.sql.SqlAndParameterBind;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.List;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/10/30
 */
public class DynamicHandlerSqlSource extends HandlerSqlSource {

    private final String[] parameterNames;

    private final FunS<SqlAndParameterBind> providerFun;

    private final SqlScriptBuilder sqlScriptBuilder;

    public DynamicHandlerSqlSource(SqlCommandType sqlCommandType, MetadataBuildingContext buildingContext, String[] parameterNames
            , FunS<SqlAndParameterBind> providerFun, SqlScriptBuilder sqlScriptBuilder) {
        super(sqlCommandType, buildingContext, null);
        this.parameterNames = parameterNames;
        this.providerFun = providerFun;
        this.sqlScriptBuilder = sqlScriptBuilder;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        Object[] params = getOriginalParameters(parameterObject, parameterNames);
        SqlAndParameterBind sqlAndParameterBind = sqlScriptBuilder.build(providerFun, params);
        List<ParameterMapping> parameterMappings = getParameterMappings(sqlAndParameterBind);
        BoundSql boundSql = new BoundSql(buildingContext.getConfiguration(), sqlHandler(sqlAndParameterBind.getSql())
                , parameterMappings, parameterObject);
        sqlAndParameterBind.getParameterBind().getParameterColumnBinds()
                .forEach(parameterColumnBind
                        -> boundSql.setAdditionalParameter(parameterColumnBind.getParameter(), parameterColumnBind.getValue()));
        return boundSql;
    }

}
