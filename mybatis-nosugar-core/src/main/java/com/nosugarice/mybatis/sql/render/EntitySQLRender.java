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

package com.nosugarice.mybatis.sql.render;

import com.nosugarice.mybatis.sql.SQLPart;
import com.nosugarice.mybatis.util.StringFormatter;
import com.nosugarice.mybatis.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.nosugarice.mybatis.sql.Placeholder.ALIAS_STATE_P;
import static com.nosugarice.mybatis.sql.Placeholder.AS_ALIAS_P;
import static com.nosugarice.mybatis.sql.Placeholder.FROM_TABLE_P;
import static com.nosugarice.mybatis.sql.Placeholder.FROM_WITH_ALIAS_P;
import static com.nosugarice.mybatis.sql.Placeholder.TABLE_ALIAS_P;
import static com.nosugarice.mybatis.sql.Placeholder.TABLE_P;
import static com.nosugarice.mybatis.sql.SQLConstants.AS_;
import static com.nosugarice.mybatis.sql.SQLConstants.DOT;
import static com.nosugarice.mybatis.sql.SQLConstants.EMPTY;
import static com.nosugarice.mybatis.sql.SQLConstants.FROM;
import static com.nosugarice.mybatis.sql.SQLConstants.SPACE;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/6/27
 */
public class EntitySQLRender {

    private final Map<String, String> placeholderValues;
    private final Map<String, String> placeholderValuesWithAlias;

    public EntitySQLRender(Builder builder) {
        this.placeholderValues = builder.buildPlaceholderValues(false);
        this.placeholderValuesWithAlias = builder.buildPlaceholderValues(true);
    }

    public String render(String sql) {
        return renderWithTableAlias(sql, false);
    }

    public String renderWithTableAlias(String sql, boolean withTableAlias) {
        return withTableAlias ? StringFormatter.replacePlaceholder(sql, placeholderValuesWithAlias)
                : StringFormatter.replacePlaceholder(sql, placeholderValues);
    }

    public static class Builder {

        private String table;
        private String schema;
        private boolean supportDynamicTableName;

        public Builder withTable(String table, String schema) {
            this.table = table;
            this.schema = schema;
            return this;
        }

        public Builder withSupportDynamicTableName(boolean supportDynamicTableName) {
            this.supportDynamicTableName = supportDynamicTableName;
            return this;
        }

        public EntitySQLRender build() {
            return new EntitySQLRender(this);
        }

        public Map<String, String> buildPlaceholderValues(boolean withTableAlias) {
            Map<String, String> placeholderValues = new HashMap<>(7);
            String tableName = supportDynamicTableName ? TABLE_P : table;
            if (StringUtils.isNotBlank(schema)) {
                tableName = schema + DOT + tableName;
            }
            String alias = SQLPart.tableAlias(table);
            String asAlias = AS_ + alias;
            String fromTable = FROM + SPACE + tableName;
            String fromWithAlias = fromTable + asAlias;
            String aliasState = alias + DOT;

            placeholderValues.put(TABLE_P, tableName);
            placeholderValues.put(FROM_TABLE_P, fromTable);

            if (withTableAlias) {
                placeholderValues.put(TABLE_ALIAS_P, alias);
                placeholderValues.put(AS_ALIAS_P, asAlias);
                placeholderValues.put(FROM_WITH_ALIAS_P, fromWithAlias);
                placeholderValues.put(ALIAS_STATE_P, aliasState);
            } else {
                placeholderValues.put(TABLE_ALIAS_P, EMPTY);
                placeholderValues.put(AS_ALIAS_P, EMPTY);
                placeholderValues.put(FROM_WITH_ALIAS_P, fromTable);
                placeholderValues.put(ALIAS_STATE_P, EMPTY);
            }
            return placeholderValues;
        }

    }

}
