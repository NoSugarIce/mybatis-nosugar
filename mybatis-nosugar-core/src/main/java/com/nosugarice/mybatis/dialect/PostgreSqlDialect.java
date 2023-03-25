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
public class PostgreSqlDialect implements Dialect {

    @Override
    public PrimaryKeyStrategy getPrimaryKeyStrategy() {
        return new PrimaryKeyStrategy() {
            @Override
            public boolean supportsAutoIncrement() {
                return true;
            }

            @Override
            public boolean supportsSelectIdentity() {
                return false;
            }

            @Override
            public String getIdentitySelectString() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean executeBeforeIdentitySelect() {
                throw new UnsupportedOperationException();
            }
        };
    }


    @Override
    public LimitHandler getLimitHandler() {
        return LimitHandlerImpl.INSTANCE;
    }

    private enum LimitHandlerImpl implements LimitHandler {

        INSTANCE;

        private static final String SQL_TEMP_LATE = "{} LIMIT {} OFFSET {}";
        private static final String NO_FIRST_ROW_SQL_TEMP_LATE = "{} LIMIT {} ";

        @Override
        public String applyLimit(String sql, int offset, int limit) {
            return hasFirstRow(offset) ? StringFormatter.format(SQL_TEMP_LATE, sql, offset, limit)
                    : StringFormatter.format(NO_FIRST_ROW_SQL_TEMP_LATE, sql, limit);
        }
    }

}
