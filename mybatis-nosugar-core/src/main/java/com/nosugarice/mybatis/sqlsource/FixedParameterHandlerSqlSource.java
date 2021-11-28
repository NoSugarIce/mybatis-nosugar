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

import com.nosugarice.mybatis.config.MetadataBuildingContext;
import com.nosugarice.mybatis.mapper.function.FunS;
import com.nosugarice.mybatis.sql.ParameterBind.ParameterColumnBind;
import com.nosugarice.mybatis.sql.SqlAndParameterBind;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.List;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/10/30
 */
public class FixedParameterHandlerSqlSource extends HandlerSqlSource {

    private final List<ParameterColumnBind> parameterColumnBinds;
    private final FunS.Param3<Object, List<ParameterColumnBind>, BoundSql, Void> parameterHandle;
    private final List<ParameterMapping> parameterMappings;

    public FixedParameterHandlerSqlSource(SqlCommandType sqlCommandType, MetadataBuildingContext buildingContext, SqlAndParameterBind sqlAndParameterBind) {
        super(sqlCommandType, buildingContext, sqlAndParameterBind.getSql());
        this.parameterColumnBinds = sqlAndParameterBind.getParameterBind().getParameterColumnBinds();
        this.parameterHandle = sqlAndParameterBind.getParameterHandle();
        this.parameterMappings = getParameterMappings(sqlAndParameterBind);
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        BoundSql boundSql = new BoundSql(buildingContext.getConfiguration(), sqlHandler(sql), parameterMappings, parameterObject);
        parameterHandle.apply(parameterObject, parameterColumnBinds, boundSql);
        return boundSql;
    }

}
