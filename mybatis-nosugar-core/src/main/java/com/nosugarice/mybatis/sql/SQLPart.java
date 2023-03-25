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

import com.nosugarice.mybatis.config.internal.NameStrategyType;
import com.nosugarice.mybatis.dialect.Dialect;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.registry.ReservedWords;
import com.nosugarice.mybatis.util.StringJoinerBuilder;
import com.nosugarice.mybatis.util.StringUtils;

import java.sql.JDBCType;

import static com.nosugarice.mybatis.sql.SQLConstants.DOT;
import static com.nosugarice.mybatis.sql.SQLConstants.EMPTY;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/1/1
 */
public interface SQLPart {

    /**
     * 预编译占位符
     *
     * @param content
     * @return
     */
    static String placeholder(String content) {
        return "#{" + content + "}";
    }

    /**
     * 直接替换占位符
     *
     * @param content
     * @return
     */
    static String placeholderDirect(String content) {
        return "${" + content + "}";
    }

    /**
     * 占位符
     *
     * @param property
     * @return
     */
    static String placeholder(RelationalProperty property) {
        return placeholder(property.getName());
    }

    /**
     * 占位符
     *
     * @param property 列
     * @param prefix   前缀
     * @return 指定参数的
     */
    static String placeholder(RelationalProperty property, String prefix) {
        return placeholder(property.getName(), prefix, property.getJdbcType(), property.getTypeHandler());
    }

    /**
     * 占位符
     *
     * @param property        列
     * @param prefix          前缀
     * @param jdbcType        jdbcType
     * @param typeHandlerType typeHandler类型
     * @return 指定参数的
     */
    static String placeholder(String property, String prefix, Integer jdbcType, Class<?> typeHandlerType) {
        return placeholder(property, prefix, assignJdbcType(jdbcType), assignTypeHandler(typeHandlerType));
    }

    /**
     * 占位符
     *
     * @param property          列
     * @param prefix            前缀
     * @param assignJdbcType    指定jdbcType
     * @param assignTypeHandler 指定typeHandler类
     * @return 指定参数的
     */
    static String placeholder(String property, String prefix, String assignJdbcType, String assignTypeHandler) {
        String content = StringJoinerBuilder.createSpaceJoin()
                .withDelimiter(EMPTY)
                .withPrefix(StringUtils.isNotBlank(prefix) ? prefix + DOT : EMPTY)
                .withElements(property)
                .withElements(StringUtils.isNotBlank(assignJdbcType), ",", assignJdbcType)
                .withElements(StringUtils.isNotBlank(assignTypeHandler), ",", assignTypeHandler)
                .build();
        return placeholder(content);
    }

    /**
     * jdbcType
     *
     * @param jdbcType
     * @return
     */
    static String assignJdbcType(Integer jdbcType) {
        if (jdbcType != null) {
            return "jdbcType=" + JDBCType.valueOf(jdbcType).getName();
        }
        return EMPTY;
    }

    /**
     * typeHandlerType
     *
     * @param typeHandlerType
     * @return
     */
    static String assignTypeHandler(Class<?> typeHandlerType) {
        if (typeHandlerType != null) {
            return "typeHandler=" + typeHandlerType.getName();
        }
        return EMPTY;
    }

    /**
     * 当有数据库关键字的时候加上
     *
     * @param column
     * @param dialect
     * @return
     */
    static String safeColumnName(String column, Dialect dialect) {
        return ReservedWords.SQL.isKeyword(column) ? dialect.escapeKeywords(column) : column;
    }


    /**
     * 包装script
     *
     * @param sql
     * @return
     */
    static String script(String sql) {
        return "<script>\n" + sql + "</script>";
    }


    /**
     * 表别名占位符+列
     *
     * @param table
     * @return
     */
    static String tableAlias(String table) {
        return NameStrategyType.FIRST_COMBINE.conversion(table) + "_";
    }

    /**
     * 合并
     *
     * @param fragments
     * @return
     */
    static String merge(String... fragments) {
        StringBuilder sql = new StringBuilder();
        for (String fragment : fragments) {
            sql.append(fragment).append(SQLConstants.SPACE);
        }
        return sql.toString();
    }

}
