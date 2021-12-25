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

package com.nosugarice.mybatis.criteria.where.criterion;

import com.nosugarice.mybatis.criteria.where.Criterion;
import com.nosugarice.mybatis.criteria.where.GroupCriterion;
import com.nosugarice.mybatis.sql.Expression;
import com.nosugarice.mybatis.sql.SQLPart;
import com.nosugarice.mybatis.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.nosugarice.mybatis.sql.SQLConstants.AND;
import static com.nosugarice.mybatis.sql.SQLConstants.EMPTY;
import static com.nosugarice.mybatis.sql.SQLConstants.OR;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class GroupCriterionImpl extends AbstractCriterion<GroupCriterionImpl> implements GroupCriterion {

    private static final long serialVersionUID = -6979194990435135901L;

    private final List<Criterion> criterionList = new ArrayList<>();

    public GroupCriterionImpl() {
    }

    public GroupCriterionImpl(Criterion... criterions) {
        append(criterions);
    }

    @Override
    public GroupCriterion append(Collection<? extends Criterion> criterions) {
        this.criterionList.addAll(criterions);
        return this;
    }

    @Override
    public GroupCriterion appendToHead(Collection<? extends Criterion> criterions) {
        this.criterionList.addAll(0, criterions);
        return this;
    }

    @Override
    public boolean hasCriterion() {
        return !criterionList.isEmpty();
    }

    @Override
    public List<Criterion> getCriterions() {
        return criterionList;
    }

    @Override
    public String getSql() {
        filterCondition();
        if (criterionList.isEmpty()) {
            return EMPTY;
        }
        String criterionSql = criterionList.stream()
                .map(Expression::getSql)
                .collect(Collectors.joining());

        if (connectorType == ConnectorType.OR || criterionSql.indexOf(OR) > 0) {
            return SQLPart.merge(getConnector(), "(", StringUtils.trim(criterionSql, Arrays.asList(AND, OR), null), ")");
        } else {
            return criterionSql;
        }

    }

}
