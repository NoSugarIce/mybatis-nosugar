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

package com.nosugarice.mybatis.dialect;

import com.nosugarice.mybatis.sql.SQLConstants;
import com.nosugarice.mybatis.util.StringUtils;

import java.util.regex.Pattern;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/11/15
 */
public interface Dialect {

    /**
     * 是否适配版本
     *
     * @param version
     * @return
     */
    default boolean supportsVersion(int version) {
        return true;
    }

    /**
     * 转义数据库关键字
     *
     * @param name
     * @return
     */
    default String escapeKeywords(String name) {
        return "\"" + name + "\"";
    }

    /**
     * 获取主键策略
     *
     * @return
     */
    PrimaryKeyStrategy getPrimaryKeyStrategy();

    /**
     * 获取分页处理方法
     *
     * @return
     */
    LimitHandler getLimitHandler();

    /**
     * 优化 Count 语句
     * TODO 先简单粗暴
     *
     * @param sql         原sql
     * @param countColumn count指定的列
     * @return
     */
    default String optimizationCountSql(String sql, String countColumn) {
        String upperCaseSql = sql.toUpperCase();
        String countSqlPart = "SELECT COUNT(*) ";
        if (StringUtils.isNotBlank(countColumn)) {
            countSqlPart = "SELECT COUNT(" + countColumn + ") ";
        }
        if (upperCaseSql.contains(SQLConstants.DISTINCT)) {
            return countSqlPart + "FROM ( " + sql + " )";
        } else {
            sql = sql.substring(upperCaseSql.indexOf(SQLConstants.FROM));
            return countSqlPart + interceptOrderBy(sql);
        }
    }

    /**
     * 优化 exists 语句
     * TODO 先简单粗暴
     *
     * @param sql 原sql
     * @return
     */
    default String optimizationExistsSql(String sql) {
        String upperCaseSql = sql.toUpperCase();
        sql = sql.substring(upperCaseSql.indexOf(SQLConstants.FROM));
        return getLimitHandler().applyLimit("SELECT 1 " + interceptOrderBy(sql), 0, 1);
    }

    /**
     * 截取条件
     * TODO 先简单粗暴
     *
     * @param sql 原sql
     * @return
     */
    default String interceptOrderBy(String sql) {
        return Pattern.compile("(?i)\\s+ORDER\\s+BY\\s+[^)]+$").matcher(sql).replaceAll("");
    }

    /**
     * 获取字面值处理器
     *
     * @return
     */
    default LiteralValueHandler getLiteralValueHandler() {
        return LiteralValueHandler.getLiteralValueHandler();
    }

}
