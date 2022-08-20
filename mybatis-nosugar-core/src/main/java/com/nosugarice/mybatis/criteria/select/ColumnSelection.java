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

package com.nosugarice.mybatis.criteria.select;

import com.nosugarice.mybatis.criteria.ColumnReader;
import com.nosugarice.mybatis.sql.Expression;
import com.nosugarice.mybatis.util.StringJoinerBuilder;
import com.nosugarice.mybatis.util.StringUtils;

import static com.nosugarice.mybatis.sql.SQLConstants.AS;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/15
 */
public class ColumnSelection implements Expression, ColumnReader {

    private static final long serialVersionUID = 7438894860588332431L;

    private final String column;
    private String alias;

    public ColumnSelection(String column) {
        this.column = column;
    }

    @SuppressWarnings("unchecked")
    public <T extends ColumnSelection> T alias(String alias) {
        this.alias = alias;
        return (T) this;
    }

    @Override
    public String getSql() {
        return StringJoinerBuilder.createSpaceJoin()
                .withElements(withTableAliasPlaceholder())
                .withElements(StringUtils.isNotEmpty(alias), AS, "\"" + alias + "\"")
                .build();
    }

    @Override
    public String getColumn() {
        return column;
    }

    public String getAlias() {
        return alias;
    }
}
