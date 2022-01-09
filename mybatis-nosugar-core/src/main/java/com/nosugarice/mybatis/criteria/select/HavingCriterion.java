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

import com.nosugarice.mybatis.criteria.ColumnReader;
import com.nosugarice.mybatis.criteria.where.Operator;
import com.nosugarice.mybatis.dialect.LiteralValueHandler;
import com.nosugarice.mybatis.sql.Expression;
import com.nosugarice.mybatis.sql.SQLConstants;
import com.nosugarice.mybatis.sql.SQLPart;
import com.nosugarice.mybatis.util.StringJoinerBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.nosugarice.mybatis.sql.SQLConstants.HAVING;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class HavingCriterion implements Expression {

    private static final long serialVersionUID = -7975724006388455833L;

    private final List<Criterion> criterionList = new ArrayList<>();

    public <V> HavingCriterion addCriterion(AggFunction function, String column, Operator operator, V value) {
        criterionList.add(new Criterion(function, column, operator, (Serializable) value));
        return this;
    }

    @Override
    public String getSql() {
        String criterionSql = criterionList.stream().map(Criterion::getSql).collect(Collectors.joining(SQLConstants.AND));
        return criterionList.size() == 1 ? SQLPart.merge(HAVING, criterionSql) : SQLPart.merge(HAVING, "(", criterionSql, ")");
    }

    private static class Criterion implements ColumnReader, Expression {

        private static final long serialVersionUID = -5011927465558888459L;

        private final AggFunction function;
        private final String column;
        private final Operator operator;
        private final Serializable value;

        public Criterion(AggFunction function, String column, Operator operator, Serializable value) {
            this.function = function;
            this.column = column;
            this.operator = operator;
            this.value = value;
        }

        @Override
        public String getColumn() {
            return column;
        }

        @Override
        public String getSql() {
            return StringJoinerBuilder.createSpaceJoin()
                    .withPrefix(function.getName())
                    .withElements("(", withTableAliasPlaceholder(), ")", operator.operator(), LiteralValueHandler.getLiteralValueHandler().convert(value))
                    .build();
        }
    }
}
