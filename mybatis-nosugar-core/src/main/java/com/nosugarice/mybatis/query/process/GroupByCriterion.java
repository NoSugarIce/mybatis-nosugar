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

package com.nosugarice.mybatis.query.process;

import com.nosugarice.mybatis.sql.Expression;
import com.nosugarice.mybatis.sql.Placeholder;
import com.nosugarice.mybatis.sql.SqlFragment;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.nosugarice.mybatis.sql.SQLConstants.GROUP_BY;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class GroupByCriterion extends SqlFragment implements Expression {

    private static final long serialVersionUID = 7685189549066444047L;

    private final String[] columns;

    public GroupByCriterion(String... columns) {
        this.columns = columns;
    }

    @Override
    public String getSql() {
        append(GROUP_BY, Stream.of(columns)
                .map(Placeholder::columnAliasState)
                .collect(Collectors.joining(",")));
        return merge();
    }

}
