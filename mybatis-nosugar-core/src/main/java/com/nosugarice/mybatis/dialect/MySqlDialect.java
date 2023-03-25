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

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/11/15
 */
public class MySqlDialect implements Dialect {

    @Override
    public String escapeKeywords(String name) {
        return "`" + name + "`";
    }

    @Override
    public PrimaryKeyStrategy getPrimaryKeyStrategy() {
        return new PrimaryKeyStrategy() {
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
                return "SELECT LAST_INSERT_ID()";
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

        @Override
        public String applyLimit(String sql, int offset, int limit) {
            return hasFirstRow(offset) ? sql + " LIMIT " + offset + ", " + limit : sql + " LIMIT " + limit;
        }
    }

}
