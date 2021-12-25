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

import com.nosugarice.mybatis.sql.Expression;
import com.nosugarice.mybatis.sql.Placeholder;
import com.nosugarice.mybatis.sql.SQLPart;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.nosugarice.mybatis.sql.SQLConstants.ASC;
import static com.nosugarice.mybatis.sql.SQLConstants.DESC;
import static com.nosugarice.mybatis.sql.SQLConstants.ORDER_BY;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class OrderByCriterion implements Expression {

    private static final long serialVersionUID = -8464077005142637010L;

    private final Map<String, Boolean> orderByColumns = new LinkedHashMap<>();

    public OrderByCriterion addCriterion(String column, boolean ascending) {
        orderByColumns.put(column, ascending);
        return this;
    }

    @Override
    public String getSql() {
        return SQLPart.merge(ORDER_BY, orderByColumns.entrySet().stream().map(entry -> SQLPart.merge(
                Placeholder.columnAliasState(entry.getKey())
                , (entry.getValue() ? ASC : DESC))).collect(Collectors.joining(",")));
    }

    public Map<String, Boolean> getOrderByColumns() {
        return orderByColumns;
    }
}
