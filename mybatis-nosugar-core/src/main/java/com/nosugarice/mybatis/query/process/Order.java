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

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class Order extends SqlFragment implements Expression<Order> {

    private static final long serialVersionUID = -8464077005142637010L;

    /** 是否正序 */
    private final boolean ascending;

    /** 列名 */
    private final String column;

    public Order(String column) {
        this.ascending = true;
        this.column = column;
    }

    public Order(boolean ascending, String column) {
        this.ascending = ascending;
        this.column = column;
    }

    @Override
    public String getSql() {
        append(column, (ascending ? "ASC" : "DESC"));
        return merge();
    }

    public boolean isAscending() {
        return ascending;
    }

    public String getColumnName() {
        return column;
    }
}
