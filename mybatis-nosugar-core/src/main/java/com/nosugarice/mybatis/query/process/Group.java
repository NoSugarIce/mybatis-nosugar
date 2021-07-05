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

import com.nosugarice.mybatis.query.SqlFragment;
import com.nosugarice.mybatis.sql.Expression;
import com.nosugarice.mybatis.util.StringUtils;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class Group extends SqlFragment implements Expression<Group> {

    private static final long serialVersionUID = 7685189549066444047L;

    private final String[] columnNames;

    public Group(String... columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public String getSql() {
        append("GROUP BY", StringUtils.join(columnNames, ","));
        return merge();
    }

}
