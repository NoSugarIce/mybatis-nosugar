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

package com.nosugarice.mybatis.sql;

import com.nosugarice.mybatis.mapper.function.FunS;
import com.nosugarice.mybatis.query.criterion.ColumnCriterion;
import com.nosugarice.mybatis.query.criterion.ColumnCriterionVisitor;
import com.nosugarice.mybatis.query.criterion.Criterion;
import com.nosugarice.mybatis.query.criterion.GroupCriterion;
import com.nosugarice.mybatis.util.CollectionUtils;
import com.nosugarice.mybatis.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.nosugarice.mybatis.sql.SQLConstants.AND;
import static com.nosugarice.mybatis.sql.SQLConstants.EMPTY;
import static com.nosugarice.mybatis.sql.SQLConstants.OR;
import static com.nosugarice.mybatis.sql.SQLConstants.SPACE;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/3/21
 */
public class CriterionSqlUtils {

    public static <S> String getCriterionSql(List<GroupCriterion> groupCriterions, ColumnCriterionVisitor<S> criterionVisitor
            , Function<S, String> visitResultHandle) {
        groupCriterions = groupCriterions.stream()
                .filter(groupCriterion -> CollectionUtils.isNotEmpty(groupCriterion.getCriterions()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(groupCriterions)) {
            return EMPTY;
        }
        String sql;
        if (groupCriterions.size() == 1) {
            sql = CriterionSqlUtils.<S>criterionsToSqlFunction()
                    .apply(groupCriterions.get(0).getCriterions(), criterionVisitor, visitResultHandle);
        } else {
            StringBuilder whereSqlBuilder = new StringBuilder();
            for (GroupCriterion groupCriterion : groupCriterions) {
                String criterionSql = CriterionSqlUtils.<S>criterionsToSqlFunction()
                        .apply(groupCriterion.getCriterions(), criterionVisitor, visitResultHandle);
                StringJoiner sqlPart = groupCriterion.getCriterions().size() > 1
                        ? new StringJoiner(SPACE, groupCriterion.getSeparator().name() + " ( ", " ) ")
                        : new StringJoiner(SPACE, groupCriterion.getSeparator().name(), SPACE);
                sqlPart.add(StringUtils.trim(criterionSql, Arrays.asList(AND, OR), null));
                whereSqlBuilder.append(sqlPart);
            }
            sql = whereSqlBuilder.toString();
        }
        //TODO 简单粗暴
        if (sql.contains(Criterion.Separator.OR.name())) {
            sql = Criterion.Separator.AND.name() + " ( " + StringUtils.trim(sql, Arrays.asList(AND, OR), null) + " ) ";
        }
        return sql;
    }

    public static <S> FunS.Param3<List<ColumnCriterion<?>>, ColumnCriterionVisitor<S>, Function<S, String>, String> criterionsToSqlFunction() {
        return (columnCriteria, criterionVisitor, visitResultHandle) -> columnCriteria.stream()
                .map(propertyCriterion -> propertyCriterion.accept(criterionVisitor))
                .map(visitResultHandle)
                .collect(Collectors.joining(SPACE));
    }

}
