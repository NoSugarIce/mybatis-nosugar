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

package com.nosugarice.mybatis.query.criteria;

import com.nosugarice.mybatis.util.StringJoinerBuilder;
import com.nosugarice.mybatis.util.StringUtils;

import static com.nosugarice.mybatis.sql.SQLConstants.AS;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public class ColumnSelection extends AbstractSelection implements ColumnReader {

    private static final long serialVersionUID = 7438894860588332431L;

    private final String column;

    public ColumnSelection(String column) {
        this.column = column;
    }

    @Override
    public boolean isColumnSelection() {
        return true;
    }

    @Override
    public String getSql() {
        return StringJoinerBuilder.createSpaceJoin()
                .withElements(withTableAliasPlaceholder())
                .withElements(StringUtils.isNotEmpty(getAlias()), AS, "\"" + getAlias() + "\"")
                .build();
    }

    @Override
    public String getColumn() {
        return column;
    }

    @Override
    public ColumnSelection getThis() {
        return this;
    }
}
