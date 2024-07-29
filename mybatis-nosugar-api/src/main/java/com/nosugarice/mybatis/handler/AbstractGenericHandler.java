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

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * 复杂类型处理器
 *
 * @author dingjingyang@foxmail.com
 * @date 2021/11/22
 */
public abstract class AbstractGenericHandler extends BaseTypeHandler<Object> {

    private final Type type;

    public AbstractGenericHandler(Type type) {
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, Optional.ofNullable(parameter).map(this::toStr).orElse(null));
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object result = rs.getString(columnName);
        return Optional.ofNullable(result).map(Object::toString).map(s -> strToObject(s, type)).orElse(null);
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object result = rs.getString(columnIndex);
        return Optional.ofNullable(result).map(Object::toString).map(s -> strToObject(s, type)).orElse(null);
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object result = cs.getString(columnIndex);
        return Optional.ofNullable(result).map(Object::toString).map(s -> strToObject(s, type)).orElse(null);
    }

    public abstract String toStr(Object parameter);

    public abstract Object strToObject(String str, Type type);


}
