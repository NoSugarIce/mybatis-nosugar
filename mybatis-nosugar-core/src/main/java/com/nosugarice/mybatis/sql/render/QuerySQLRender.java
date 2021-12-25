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

package com.nosugarice.mybatis.sql.render;

import com.nosugarice.mybatis.criteria.select.JoinCriteria;
import com.nosugarice.mybatis.criteria.select.QueryStructure;
import com.nosugarice.mybatis.sql.Expression;
import com.nosugarice.mybatis.sql.ParameterBind;
import com.nosugarice.mybatis.util.StringJoinerBuilder;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.nosugarice.mybatis.sql.Placeholder.AS_ALIAS_P;
import static com.nosugarice.mybatis.sql.Placeholder.TABLE_P;
import static com.nosugarice.mybatis.sql.SQLConstants.EMPTY;
import static com.nosugarice.mybatis.sql.SQLConstants.FROM;


/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public class QuerySQLRender extends WhereSQLRender {

    private final QueryStructure<?> queryStructure;

    public QuerySQLRender(QueryStructure<?> queryStructure) {
        super(queryStructure);
        this.queryStructure = queryStructure;
    }

    public String renderColumnSelect() {
        return Optional.of(queryStructure).map(QueryStructure::getColumnSelections).flatMap(Function.identity())
                .map(functionSelections -> functionSelections.stream()
                        .map(Expression::getSql).collect(Collectors.joining(", ", EMPTY, ",")))
                .orElse(EMPTY);
    }

    public String renderFunctionSelect() {
        return Optional.of(queryStructure).map(QueryStructure::getFunctionSelections).flatMap(Function.identity())
                .map(functionSelections -> functionSelections.stream()
                        .map(Expression::getSql).collect(Collectors.joining(", ", EMPTY, ",")))
                .orElse(EMPTY);
    }

    public String renderFrom() {
        return StringJoinerBuilder.createSpaceJoin()
                .withElements(FROM, TABLE_P, AS_ALIAS_P)
                .build();
    }

    public String renderGroupBy() {
        return Optional.of(queryStructure).map(QueryStructure::getGroupBy).flatMap(Function.identity())
                .map(Expression::getSql).orElse(EMPTY);
    }

    public String renderHaving() {
        return Optional.of(queryStructure).map(QueryStructure::getHaving).flatMap(Function.identity())
                .map(Expression::getSql).orElse(EMPTY);
    }

    public String renderOrderBy() {
        return Optional.of(queryStructure).map(QueryStructure::getOrderBy).flatMap(Function.identity())
                .map(Expression::getSql).orElse(EMPTY);
    }

    public String renderJoinSelect() {
        return Optional.of(queryStructure).map(QueryStructure::getJoinCriteria).flatMap(Function.identity())
                .map(JoinCriteria::getSelectionSql).orElse(EMPTY);
    }

    public String renderJoinFom() {
        return Optional.of(queryStructure).map(QueryStructure::getJoinCriteria).flatMap(Function.identity())
                .map(JoinCriteria::getJoinSql).orElse(EMPTY);
    }

    public String renderJoinWhere(ParameterBind parameterBind) {
        return Optional.of(queryStructure).map(QueryStructure::getJoinCriteria).flatMap(Function.identity())
                .map(joinCriteria -> joinCriteria.getWhereSql(parameterBind)).orElse(EMPTY);
    }

}
