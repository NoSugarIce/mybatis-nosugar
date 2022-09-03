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

import com.nosugarice.mybatis.criteria.criterion.JoinCriterion;
import com.nosugarice.mybatis.registry.EntityMetadataRegistry;
import com.nosugarice.mybatis.sql.ParameterBind;
import com.nosugarice.mybatis.sql.SQLPart;
import com.nosugarice.mybatis.sql.render.EntitySQLRender;
import com.nosugarice.mybatis.sql.render.JoinQuerySQLRender;
import com.nosugarice.mybatis.util.StringJoinerBuilder;
import com.nosugarice.mybatis.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.nosugarice.mybatis.sql.SQLConstants.DOT;
import static com.nosugarice.mybatis.sql.SQLConstants.EQUALS_TO;
import static com.nosugarice.mybatis.sql.SQLConstants.LINE_SEPARATOR;
import static com.nosugarice.mybatis.sql.SQLConstants.ON;
import static com.nosugarice.mybatis.sql.SQLConstants.SPACE;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public class JoinCriteria<T> {

    private final String masterTableAlias;

    private final List<JoinCriterion<?, ?, ?, ?>> joinCriterionList = new ArrayList<>();
    private final Map<Integer, JoinQuerySQLRender> joinCriterionMap = new HashMap<>();

    public JoinCriteria(Class<T> masterType) {
        String masterTable = EntityMetadataRegistry.getInstance().getTable(masterType);
        this.masterTableAlias = SQLPart.tableAlias(masterTable);
    }

    public void addJoinCriterion(JoinCriterion<?, ?, ?, ?> joinCriterion) {
        joinCriterionList.add(joinCriterion);
        joinCriterionMap.put(Objects.hash(joinCriterion), new JoinQuerySQLRender((QueryStructure<?>) joinCriterion, new EntitySQLRender.Builder()
                .withTable(joinCriterion.getTable(), joinCriterion.getSchema())
                .withSupportDynamicTableName(false)
                .build()));
    }

    public String getSelectionSql() {
        return joinCriterionList.stream()
                .map(joinCriterion -> joinCriterionMap.get(Objects.hash(joinCriterion)))
                .map(JoinQuerySQLRender::renderColumnSelect)
                .collect(Collectors.joining());
    }

    public String getJoinSql() {
        return joinCriterionList.stream()
                .map(joinCriterion -> StringJoinerBuilder.createSpaceJoin()
                        .withPrefix(SPACE)
                        .withSuffix(LINE_SEPARATOR)
                        .withElements(joinCriterion.getJoinType().getName())
                        .withElements(StringUtils.isNotBlank(joinCriterion.getSchema()), joinCriterion.getSchema() + DOT)
                        .withElements(joinCriterion.getTable(), joinCriterion.getTableAlias(), ON)
                        .withElements(masterTableAlias + DOT + joinCriterion.getMasterColumn())
                        .withElements(EQUALS_TO, joinCriterion.getTableAlias() + DOT + joinCriterion.getColumn())
                        .build())
                .collect(Collectors.joining(SPACE));
    }

    public String getWhereSql(ParameterBind parameterBind) {
        return joinCriterionList.stream()
                .map(joinCriterion -> joinCriterionMap.get(Objects.hash(joinCriterion)))
                .map(render -> render.renderWhere(parameterBind))
                .collect(Collectors.joining(SPACE));
    }

}
