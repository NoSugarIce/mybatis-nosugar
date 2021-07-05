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

package com.nosugarice.mybatis.builder.sql;

import com.nosugarice.mybatis.config.Supports;
import com.nosugarice.mybatis.mapping.RelationalEntity;
import com.nosugarice.mybatis.support.DynamicTableNameMapping;
import com.nosugarice.mybatis.support.NameStrategyType;
import com.nosugarice.mybatis.util.StringFormatter;
import com.nosugarice.mybatis.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/6/27
 */
public class SqlRender {

    private final RelationalEntity entity;
    private final Supports supports;
    private final boolean withTableAlias;

    private final Map<String, String> placeholderValues;

    public SqlRender(Builder builder) {
        this.entity = builder.entity;
        this.supports = builder.supports;
        this.withTableAlias = builder.withTableAlias;
        this.placeholderValues = builder.placeholderValues;
    }

    public String renderWithTableAlias(String sql) {
        return StringFormatter.replacePlaceholder(sql, placeholderValues);
    }

    public String renderWithTableAlias(String sql, boolean withTableAlias) {
        Map<String, String> placeholderValues = this.placeholderValues;
        if (withTableAlias != this.withTableAlias) {
            placeholderValues = new Builder()
                    .withEntity(entity)
                    .withSupports(supports)
                    .withTableAlias(withTableAlias)
                    .build().placeholderValues;
        }
        return StringFormatter.replacePlaceholder(sql, placeholderValues);
    }

    public static class Builder {

        private RelationalEntity entity;
        private Supports supports;
        private boolean withTableAlias;

        private final Map<String, String> placeholderValues = new HashMap<>();

        public Builder withEntity(RelationalEntity entity) {
            this.entity = entity;
            return this;
        }

        public Builder withSupports(Supports supports) {
            this.supports = supports;
            return this;
        }

        public Builder withTableAlias(boolean withTableAlias) {
            this.withTableAlias = withTableAlias;
            return this;
        }

        public SqlRender build() {
            String tableName = supports.isSupportDynamicTableName()
                    ? SqlPart.tagBind("tableName", "@" + DynamicTableNameMapping.class.getName()
                    + "@" + DynamicTableNameMapping.TABLE_NAME_METHOD + "('" + entity.getTable().getName() + "')") + " ${tableName}"
                    : entity.getTable().getName();
            String fromTable = SqlPart.FROM + tableName;
            String alias = supports.isSupportSqlUseAlias() && withTableAlias ? NameStrategyType.FIRST_COMBINE.conversion(entity.getName()) : SqlPart.EMPTY;
            String asAlias = StringUtils.isNotEmpty(alias) ? SqlPart.AS + alias : SqlPart.EMPTY;
            String fromWithAlias = fromTable + asAlias;
            String aliasState = StringUtils.isNotBlank(alias) ? alias + SqlPart.DOT : SqlPart.EMPTY;

            placeholderValues.put(SqlPart.Placeholder.TABLE_NAME, tableName);
            placeholderValues.put(SqlPart.Placeholder.FROM_TABLE, fromTable);
            placeholderValues.put(SqlPart.Placeholder.TABLE_NAME_ALIAS, alias);
            placeholderValues.put(SqlPart.Placeholder.AS_ALIAS, asAlias);
            placeholderValues.put(SqlPart.Placeholder.FROM_WITH_ALIAS, fromWithAlias);
            placeholderValues.put(SqlPart.Placeholder.ALIAS_STATE, aliasState);
            return new SqlRender(this);
        }

    }

}
