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
public class ParameterValueHandler<T> implements TypeHandler<T> {

    private final TypeHandler<T> typeHandler;
    private final ValueHandler<T> valueHandler;

    public ParameterValueHandler(TypeHandler<T> typeHandler, ValueHandler<T> valueHandler) {
        this.typeHandler = typeHandler;
        this.valueHandler = valueHandler;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        typeHandler.setParameter(ps, i, valueHandler.setValue(parameter), jdbcType);
    }

    @Override
    public T getResult(ResultSet rs, String columnName) throws SQLException {
        return typeHandler.getResult(rs, columnName);
    }

    @Override
    public T getResult(ResultSet rs, int columnIndex) throws SQLException {
        return typeHandler.getResult(rs, columnIndex);
    }

    @Override
    public T getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return typeHandler.getResult(cs, columnIndex);
    }

}
