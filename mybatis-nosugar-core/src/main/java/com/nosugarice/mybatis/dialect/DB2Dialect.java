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
 * @author dingjingyang@foxmail.com
 * @date 2020/11/15
 */
public class DB2Dialect implements Dialect {

    @Override
    public PrimaryKeyStrategy getPrimaryKeyStrategy() {
        return new PrimaryKeyStrategy() {
            @Override
            public boolean supportsAutoIncrement() {
                return false;
            }

            @Override
            public boolean supportsSelectIdentity() {
                return true;
            }

            @Override
            public String getIdentitySelectString() {
                return "VALUES IDENTITY_VAL_LOCAL()";
            }

            @Override
            public boolean executeBeforeIdentitySelect() {
                return false;
            }
        };
    }

    @Override
    public LimitHandler getLimitHandler() {
        return LimitHandlerImpl.INSTANCE;
    }

    private enum LimitHandlerImpl implements LimitHandler {

        INSTANCE;

        private static final String SQL_TEMP_LATE = "" +
                "SELECT\n" +
                "   * \n" +
                "FROM\n" +
                "   (\n" +
                "   SELECT\n" +
                "       INNER2_.*,\n" +
                "       ROWNUMBER ( ) OVER ( ORDER BY ORDER of INNER2_ ) AS ROWNUMBER_ \n" +
                "   FROM\n" +
                "       ( {} FETCH FIRST {} ROWS ONLY ) AS INNER2_ \n" +
                "   ) AS INNER1_ \n" +
                "WHERE\n" +
                "   ROWNUMBER_ > {} \n" +
                "ORDER BY\n" +
                "   ROWNUMBER_";

        private static final String NO_FIRST_ROW_SQL_TEMP_LATE = "" +
                "{} FETCH FIRST {} ROWS ONLY";

        @Override
        public String applyLimit(String sql, int offset, int limit) {
            return hasFirstRow(offset) ? StringFormatter.format(SQL_TEMP_LATE, sql, offset + limit, offset)
                    : StringFormatter.format(NO_FIRST_ROW_SQL_TEMP_LATE, sql, limit);
        }
    }

}
