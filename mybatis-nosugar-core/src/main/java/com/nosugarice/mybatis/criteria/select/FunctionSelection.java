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

import com.nosugarice.mybatis.util.StringJoinerBuilder;
import com.nosugarice.mybatis.util.StringUtils;

import static com.nosugarice.mybatis.sql.SQLConstants.AS;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/16
 */
public class FunctionSelection extends ColumnSelection implements SQLFunction {

    private static final long serialVersionUID = -2558470473322228603L;

    private final String name;

    public FunctionSelection(String name, String column) {
        super(column);
        this.name = name;
    }

    @Override
    public String getSql() {
        return StringJoinerBuilder.createSpaceJoin()
                .withPrefix(getName())
                .withElements("(", withTableAliasPlaceholder(), ")")
                .withElements(StringUtils.isNotEmpty(getAlias()), AS, "\"" + getAlias() + "\"")
                .build();
    }

    @Override
    public String getName() {
        return name;
    }

}
