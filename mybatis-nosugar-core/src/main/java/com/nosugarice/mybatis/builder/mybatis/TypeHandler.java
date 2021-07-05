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

package com.nosugarice.mybatis.builder.mybatis;

import com.nosugarice.mybatis.query.criterion.Like;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.StringTypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author dingjingyang@foxmail.com(dingjingyang)
 * @date 2021/3/8
 */
public class TypeHandler {

    @MappedJdbcTypes(value = JdbcType.OTHER)
    public static class StartLikeTypeHandler extends StringTypeHandler {
        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
            super.setNonNullParameter(ps, i, Like.MatchMode.START.toMatchString(parameter), jdbcType);
        }
    }

    @MappedJdbcTypes(value = JdbcType.OTHER)
    public static class EenLikeTypeHandler extends StringTypeHandler {
        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
            super.setNonNullParameter(ps, i, Like.MatchMode.END.toMatchString(parameter), jdbcType);
        }
    }

    @MappedJdbcTypes(value = JdbcType.OTHER)
    public static class AnywhereLikeTypeHandler extends StringTypeHandler {
        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
            super.setNonNullParameter(ps, i, Like.MatchMode.ANYWHERE.toMatchString(parameter), jdbcType);
        }
    }

}
