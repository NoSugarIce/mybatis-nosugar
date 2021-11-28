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

import com.nosugarice.mybatis.mapper.MapperParam;
import com.nosugarice.mybatis.query.criteria.JoinCriteria;
import com.nosugarice.mybatis.query.criteria.QueryStructure;
import com.nosugarice.mybatis.query.criterion.ColumnCriterionVisitor;
import com.nosugarice.mybatis.sql.CriterionSqlUtils;
import com.nosugarice.mybatis.sql.Expression;
import com.nosugarice.mybatis.sql.ParameterBind;
import com.nosugarice.mybatis.sql.SqlAndParameterBind;
import com.nosugarice.mybatis.util.StringJoinerBuilder;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.nosugarice.mybatis.sql.Placeholder.TABLE_ALIAS_P;
import static com.nosugarice.mybatis.sql.Placeholder.TABLE_P;
import static com.nosugarice.mybatis.sql.SQLConstants.AS;
import static com.nosugarice.mybatis.sql.SQLConstants.EMPTY;
import static com.nosugarice.mybatis.sql.SQLConstants.FROM;


/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public class CriteriaQuerySQLRender {

    private final EntitySQLRender entitySQLRender;

    private final QueryStructure<?> queryStructure;

    private static final String PREFIX = MapperParam.CRITERIA + ".parameterBinds";

    public CriteriaQuerySQLRender(QueryStructure<?> queryStructure, EntitySQLRender entitySQLRender) {
        this.queryStructure = queryStructure;
        this.entitySQLRender = entitySQLRender;
    }

    public String renderColumnSelect() {
        return Optional.of(queryStructure).map(QueryStructure::getColumnSelections).flatMap(Function.identity())
                .map(functionSelections -> functionSelections.stream()
                        .map(Expression::getSql).collect(Collectors.joining(", ", EMPTY, ",")))
                .map(this::replacePlaceholder).orElse(null);
    }

    public String renderFunctionSelect() {
        return Optional.of(queryStructure).map(QueryStructure::getFunctionSelections).flatMap(Function.identity())
                .map(functionSelections -> functionSelections.stream()
                        .map(Expression::getSql).collect(Collectors.joining(", ", EMPTY, ",")))
                .map(this::replacePlaceholder).orElse(null);
    }

    public String renderFrom() {
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(FROM, TABLE_P, AS, TABLE_ALIAS_P)
                .build();
        return replacePlaceholder(sql);
    }

    public String renderWhere(ParameterBind parameterBind) {
        return Optional.of(queryStructure)
                .filter(QueryStructure::hasCriterion)
                .map(QueryStructure::getGroupCriterions)
                .flatMap(Function.identity())
                .map(groupCriterions -> {
                    ColumnCriterionVisitor<SqlAndParameterBind> visitor = new PreparedVisitor(queryStructure.getFromType(), parameterBind, PREFIX);
                    return CriterionSqlUtils.getCriterionSql(groupCriterions, visitor, SqlAndParameterBind::getSql);
                })
                .map(this::replacePlaceholder)
                .orElse(EMPTY);
    }

    public String renderGroupBy() {
        return Optional.of(queryStructure).map(QueryStructure::getGroupBy).flatMap(Function.identity())
                .map(Expression::getSql).map(this::replacePlaceholder).orElse(null);
    }

    public String renderHaving() {
        return Optional.of(queryStructure).map(QueryStructure::getHaving).flatMap(Function.identity())
                .map(Expression::getSql).map(this::replacePlaceholder).orElse(null);
    }

    public String renderOrderBy() {
        return Optional.of(queryStructure).map(QueryStructure::getSort).flatMap(Function.identity())
                .map(Expression::getSql).map(this::replacePlaceholder).orElse(null);
    }

    public String renderJoinSelect() {
        return Optional.of(queryStructure).map(QueryStructure::getJoinCriteria).flatMap(Function.identity())
                .map(JoinCriteria::getSelectionSql).map(this::replacePlaceholder).orElse(null);
    }

    public String renderJoinFom() {
        return Optional.of(queryStructure).map(QueryStructure::getJoinCriteria).flatMap(Function.identity())
                .map(JoinCriteria::getJoinSql).map(this::replacePlaceholder).orElse(null);
    }

    public String renderJoinWhere(ParameterBind parameterBind) {
        return Optional.of(queryStructure).map(QueryStructure::getJoinCriteria).flatMap(Function.identity())
                .map(joinCriteria -> joinCriteria.getWhereSql(parameterBind)).map(this::replacePlaceholder).orElse(null);
    }

    private String replacePlaceholder(String sql) {
        return entitySQLRender.renderWithTableAlias(sql, true);
    }

}
