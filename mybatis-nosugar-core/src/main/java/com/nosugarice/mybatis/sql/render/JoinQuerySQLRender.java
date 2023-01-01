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

import com.nosugarice.mybatis.criteria.select.QueryStructure;
import com.nosugarice.mybatis.sql.ParameterBind;


/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public class JoinQuerySQLRender extends QuerySQLRender {

    private final EntitySQLRender entitySQLRender;

    public JoinQuerySQLRender(QueryStructure<?> queryStructure, EntitySQLRender entitySQLRender) {
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
        return entitySQLRender.renderWithTableAlias(sql, true);
    }

}
