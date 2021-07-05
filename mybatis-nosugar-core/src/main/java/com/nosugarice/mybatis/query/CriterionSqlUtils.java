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

package com.nosugarice.mybatis.query;

import com.nosugarice.mybatis.builder.sql.SqlPart;
import com.nosugarice.mybatis.sql.criterion.Criterion;
import com.nosugarice.mybatis.sql.criterion.GroupCriterion;
import com.nosugarice.mybatis.sql.criterion.PropertyCriterion;
import com.nosugarice.mybatis.sql.criterion.PropertyCriterionVisitor;
import com.nosugarice.mybatis.util.CollectionUtils;
import com.nosugarice.mybatis.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dingjingyang@foxmail.com(dingjingyang)
 * @date 2021/3/21
 */
public class CriterionSqlUtils {

    public static String getCriterionSql(PropertyCriterionVisitor<String> criterionVisitor, List<GroupCriterion> groupCriterions) {
        Function<List<PropertyCriterion<?>>, String> criterionsToSql = criteria -> criteria.stream()
                .map(propertyCriterion -> propertyCriterion.accept(criterionVisitor))
                .collect(Collectors.joining());

        groupCriterions = groupCriterions.stream()
                .filter(groupCriterion -> CollectionUtils.isNotEmpty(groupCriterion.getCriterions()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(groupCriterions)) {
            return SqlPart.EMPTY;
        }
        if (groupCriterions.size() == 1) {
            return criterionsToSql.apply(groupCriterions.get(0).getCriterions());
        } else {
            StringBuilder whereSqlBuilder = new StringBuilder();
            for (GroupCriterion groupCriterion : groupCriterions) {
                String criterionSql = criterionsToSql.apply(groupCriterion.getCriterions());
                StringJoiner sqlPart = new StringJoiner(SqlPart.SPACE, groupCriterion.getSeparator().name() + "(", ")");
                sqlPart.add(StringUtils.trim(criterionSql, Arrays.asList(Criterion.Separator.AND.name()
                        , Criterion.Separator.OR.name()), null));
                whereSqlBuilder.append(sqlPart);
            }
            return whereSqlBuilder.toString();
        }
    }

}
