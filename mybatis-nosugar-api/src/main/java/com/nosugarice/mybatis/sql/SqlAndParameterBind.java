/*
 * Copyright 2021-2023 NoSugarIce
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nosugarice.mybatis.sql;

import com.nosugarice.mybatis.mapper.function.FunS;
import com.nosugarice.mybatis.sql.ParameterBind.ParameterColumnBind;
import org.apache.ibatis.mapping.BoundSql;

import java.util.ArrayList;
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

    private List<FunS.Param3<Object, List<ParameterColumnBind>, BoundSql, Void>> parameterHandles;

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
        this.parameters = sqlAndParameterBind.getParameters();
        if (sqlAndParameterBind.hasParameterHandle()) {
            this.parameterHandles = new ArrayList<>(sqlAndParameterBind.getParameterHandles());
        }
    }

    public ParameterColumnBind bind(Object value, String column, Class<?> entityClass) {
        return parameterBind.bindValue(value, column, entityClass);
    }

    public ParameterColumnBind bindConditionValue(Object value, String column, Class<?> entityClass) {
        return parameterBind.bindConditionValue(value, column, entityClass);
    }

    public SqlAndParameterBind addParameterHandle(FunS.Param3<Object, List<ParameterColumnBind>, BoundSql, Void> parameterHandle) {
        if (parameterHandle == null) {
            return this;
        }
        if (parameterHandles == null) {
            parameterHandles = new ArrayList<>();
        }
        parameterHandles.add(parameterHandle);
        return this;
    }

    public boolean hasParameterHandle() {
        return parameterHandles != null && !parameterHandles.isEmpty();
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

    public List<FunS.Param3<Object, List<ParameterColumnBind>, BoundSql, Void>> getParameterHandles() {
        return parameterHandles;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
}
