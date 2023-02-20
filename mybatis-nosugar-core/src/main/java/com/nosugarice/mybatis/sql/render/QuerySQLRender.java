/*
 * Copyright 2021-2023 NoSugarIce
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nosugarice.mybatis.sql.render;

import com.nosugarice.mybatis.criteria.criterion.JoinCriterion;
import com.nosugarice.mybatis.criteria.select.JoinCriteria;
import com.nosugarice.mybatis.criteria.select.QueryStructure;
import com.nosugarice.mybatis.sql.Expression;
import com.nosugarice.mybatis.sql.ParameterBind;
import com.nosugarice.mybatis.util.StringJoinerBuilder;
import com.nosugarice.mybatis.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.nosugarice.mybatis.sql.Placeholder.AS_ALIAS_P;
import static com.nosugarice.mybatis.sql.Placeholder.TABLE_P;
import static com.nosugarice.mybatis.sql.SQLConstants.DOT;
import static com.nosugarice.mybatis.sql.SQLConstants.EMPTY;
import static com.nosugarice.mybatis.sql.SQLConstants.EQUALS_TO;
import static com.nosugarice.mybatis.sql.SQLConstants.FROM;
import static com.nosugarice.mybatis.sql.SQLConstants.LINE_SEPARATOR;
import static com.nosugarice.mybatis.sql.SQLConstants.ON;
import static com.nosugarice.mybatis.sql.SQLConstants.SPACE;


/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public class QuerySQLRender extends WhereSQLRender {

    protected final QueryStructure queryStructure;

    private final Optional<JoinSQLRender> joinSQLRenderOptional;

    public QuerySQLRender(QueryStructure queryStructure) {
        super(queryStructure);
        this.queryStructure = queryStructure;
        this.joinSQLRenderOptional = queryStructure.getJoinCriteria().map(JoinSQLRender::new);
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
        return joinSQLRenderOptional.map(JoinSQLRender::getSelectionSql).orElse(EMPTY);
    }

    public String renderJoinFom() {
        return joinSQLRenderOptional.map(JoinSQLRender::getJoinSql).orElse(EMPTY);
    }

    public String renderJoinWhere(ParameterBind parameterBind) {
        return joinSQLRenderOptional.map(joinSQLRender -> joinSQLRender.getWhereSql(parameterBind)).orElse(EMPTY);
    }

    private class JoinSQLRender {

        private final JoinCriteria joinCriteria;

        private final Map<JoinCriterion<?, ?, ?, ?>, JoinQuerySQLRender> joinCriterionMap = new HashMap<>();

        public final Function<JoinCriterion<?, ?, ?, ?>, JoinQuerySQLRender> joinQuerySQLRenderFunction =
                joinCriterion -> new JoinQuerySQLRender((QueryStructure) joinCriterion, new EntitySQLRender.Builder()
                        .withTable(joinCriterion.getTable(), joinCriterion.getSchema())
                        .withSupportDynamicTableName(false)
                        .build());

        public JoinSQLRender(JoinCriteria joinCriteria) {
            this.joinCriteria = joinCriteria;
        }

        public String getSelectionSql() {
            return joinCriteria.getJoinCriterionList().stream()
                    .map(joinCriterion -> joinCriterionMap.computeIfAbsent(joinCriterion, joinQuerySQLRenderFunction))
                    .map(JoinQuerySQLRender::renderColumnSelect)
                    .collect(Collectors.joining());
        }

        public String getJoinSql() {
            return joinCriteria.getJoinCriterionList().stream()
                    .map(joinCriterion -> StringJoinerBuilder.createSpaceJoin()
                            .withPrefix(SPACE)
                            .withSuffix(LINE_SEPARATOR)
                            .withElements(joinCriterion.getJoinType().getName())
                            .withElements(StringUtils.isNotBlank(joinCriterion.getSchema()), joinCriterion.getSchema() + DOT)
                            .withElements(joinCriterion.getTable(), ((QueryStructure) joinCriterion).getTableAlias(), ON)
                            .withElements(joinCriteria.getMasterTableAlias() + DOT + joinCriterion.getMasterColumn())
                            .withElements(EQUALS_TO, ((QueryStructure) joinCriterion).getTableAlias() + DOT + joinCriterion.getColumn())
                            .build())
                    .collect(Collectors.joining(SPACE));
        }

        public String getWhereSql(ParameterBind parameterBind) {
            return joinCriteria.getJoinCriterionList().stream()
                    .map(joinCriterion -> joinCriterionMap.computeIfAbsent(joinCriterion, joinQuerySQLRenderFunction))
                    .map(render -> render.renderWhere(parameterBind))
                    .collect(Collectors.joining(SPACE));
        }

        private class JoinQuerySQLRender extends QuerySQLRender {

            private final EntitySQLRender entitySQLRender;

            public JoinQuerySQLRender(QueryStructure queryStructure, EntitySQLRender entitySQLRender) {
                super(queryStructure);
                this.entitySQLRender = entitySQLRender;
            }

            @Override
            public String renderColumnSelect() {
                return replacePlaceholder(super.renderColumnSelect());
            }

            @Override
            public String renderFunctionSelect() {
                return replacePlaceholder(super.renderFunctionSelect());
            }

            @Override
            public String renderFrom() {
                return replacePlaceholder(super.renderFrom());
            }

            @Override
            public String renderWhere(ParameterBind parameterBind) {
                return replacePlaceholder(super.renderWhere(parameterBind));
            }

            @Override
            public String renderGroupBy() {
                return replacePlaceholder(super.renderGroupBy());
            }

            @Override
            public String renderHaving() {
                return replacePlaceholder(super.renderHaving());
            }

            @Override
            public String renderOrderBy() {
                return replacePlaceholder(super.renderOrderBy());
            }

            @Override
            public String renderJoinSelect() {
                return replacePlaceholder(super.renderJoinSelect());
            }

            @Override
            public String renderJoinFom() {
                return replacePlaceholder(super.renderJoinFom());
            }

            @Override
            public String renderJoinWhere(ParameterBind parameterBind) {
                return replacePlaceholder(super.renderJoinWhere(parameterBind));
            }

            private String replacePlaceholder(String sql) {
                return entitySQLRender.renderWithTableAlias(sql, JoinQuerySQLRender.this.queryStructure.getTableAlias());
            }

        }

    }

}
