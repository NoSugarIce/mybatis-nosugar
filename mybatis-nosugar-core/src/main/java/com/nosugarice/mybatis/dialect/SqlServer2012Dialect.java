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

import com.nosugarice.mybatis.util.StringFormatter;

/**
 * SqlServer 不熟,可能会有问题
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/11/15
 */
public class SqlServer2012Dialect extends SqlServerDialect {

    @Override
    public boolean supportsVersion(int version) {
        return version > 8;
    }

    @Override
    public LimitHandler getLimitHandler() {
        return LimitHolder.LIMIT_HANDLER_INSTANCE;
    }

    private static class LimitHolder {
        private static final LimitHandler LIMIT_HANDLER_INSTANCE = new SqlServer2012LimitHandler();
    }

    public static class SqlServer2012LimitHandler extends SqlServerLimitHandler {

        private static final String ORDER_BY_SQL_TEMP_LATE = "{} OFFSET {} ROWS FETCH NEXT {} ROWS ONLY";

        @Override
        public String applyLimit(String sql, int offset, int limit) {
            if (!hasFirstRow(offset)) {
                return StringFormatter.format(noFirstRowSqlTempLate(), limit, removeSelect(sql));
            }
            if (!haseOrderBy(sql)) {
                return StringFormatter.format(noOrderBySqlTempLate(), sql, offset, offset + limit);
            }
            return StringFormatter.format(orderBySqlTempLate(), sql, offset, limit);
        }

        @Override
        public String orderBySqlTempLate() {
            return ORDER_BY_SQL_TEMP_LATE;
        }

    }

}
