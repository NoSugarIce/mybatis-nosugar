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

package com.nosugarice.mybatis.dialect;

import com.nosugarice.mybatis.util.StringFormatter;

/**
 * SqlServer 不熟,可能会有问题
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/11/15
 */
public class SqlServerDialect implements Dialect {

    @Override
    public boolean supportsVersion(int version) {
        return version <= 8;
    }

    @Override
    public String processKeywords(String name) {
        return "[" + name + "]";
    }

    @Override
    public Identity getIdentity() {
        return new Identity() {
            @Override
            public boolean supportsAutoIncrement() {
                return true;
            }

            @Override
            public boolean supportsSelectIdentity() {
                return true;
            }

            @Override
            public String getIdentitySelectString() {
                return "SELECT SCOPE_IDENTITY()";
            }

            @Override
            public boolean executeBeforeIdentitySelect() {
                return false;
            }
        };
    }

    @Override
    public Limitable getLimitHandler() {
        return LimitHolder.LIMITABLE_INSTANCE;
    }

    private static class LimitHolder {
        private static final Limitable LIMITABLE_INSTANCE = new SqlServerLimitable();
    }

    public static class SqlServerLimitable implements Limitable {

        private static final String ORDER_BY = "ORDER BY";

        private static final String NO_FIRST_ROW_SQL_TEMP_LATE = "" +
                "SELECT TOP({}) {}";
        private static final String NO_ORDER_BY_SQL_TEMP_LATE = "" +
                "WITH QUERY AS (\n" +
                "   SELECT\n" +
                "       INNER_QUERY.*,\n" +
                "       ROW_NUMBER ( ) OVER ( ORDER BY CURRENT_TIMESTAMP ) AS __ROW_NR__ \n" +
                "   FROM\n" +
                "       ( {} ) INNER_QUERY \n" +
                "   ) SELECT\n" +
                "   * \n" +
                "FROM\n" +
                "QUERY \n" +
                "WHERE\n" +
                "   __ROW_NR__ >= {} \n" +
                "   AND __ROW_NR__ < {}";

        private static final String ORDER_BY_SQL_TEMP_LATE = "" +
                "WITH QUERY AS (\n" +
                "   SELECT\n" +
                "       INNER_QUERY.*,\n" +
                "       ROW_NUMBER ( ) OVER ( ORDER BY CURRENT_TIMESTAMP ) AS __ROW_NR__ \n" +
                "   FROM\n" +
                "       ( SELECT TOP ({}) {} ) INNER_QUERY \n" +
                "   ) SELECT\n" +
                "   * \n" +
                "FROM\n" +
                "QUERY \n" +
                "WHERE\n" +
                "   __ROW_NR__ >= {} \n" +
                "   AND __ROW_NR__ < {}";

        @Override
        public boolean supportsLimit() {
            return true;
        }

        @Override
        public String processSql(String sql, int offset, int limit) {
            boolean hasFirstRow = hasFirstRow(offset);
            if (!hasFirstRow) {
                return StringFormatter.format(noFirstRowSqlTempLate(), limit, removeSelect(sql));
            }
            if (!haseOrderBy(sql)) {
                return StringFormatter.format(noOrderBySqlTempLate(), sql, offset, offset + limit);
            }
            return StringFormatter.format(orderBySqlTempLate(), limit, removeSelect(sql), offset, offset + limit);
        }

        public String noFirstRowSqlTempLate() {
            return NO_FIRST_ROW_SQL_TEMP_LATE;
        }

        public String noOrderBySqlTempLate() {
            return NO_ORDER_BY_SQL_TEMP_LATE;
        }

        public String orderBySqlTempLate() {
            return ORDER_BY_SQL_TEMP_LATE;
        }

        public boolean haseOrderBy(String sql) {
            return sql.toUpperCase().contains(ORDER_BY);
        }

        public String getOrderBy(String sql) {
            if (haseOrderBy(sql)) {
                return sql.substring(sql.toUpperCase().indexOf(ORDER_BY));
            }
            return "";
        }

        public String removeSelect(String sql) {
            return sql.substring("SELECT".length());
        }

    }

}
