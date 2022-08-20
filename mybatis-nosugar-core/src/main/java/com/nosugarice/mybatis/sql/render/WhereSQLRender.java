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

import com.nosugarice.mybatis.criteria.criterion.CriterionSQLVisitor;
import com.nosugarice.mybatis.criteria.criterion.GroupCriterion;
import com.nosugarice.mybatis.criteria.where.WhereStructure;
import com.nosugarice.mybatis.sql.ParameterBind;
import com.nosugarice.mybatis.sql.SqlAndParameterBind;
import com.nosugarice.mybatis.sql.vistor.PreparedSQLVisitor;

import java.util.Optional;
import java.util.function.Function;

import static com.nosugarice.mybatis.sql.SQLConstants.EMPTY;


/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public class WhereSQLRender {

    private final WhereStructure structure;

    public WhereSQLRender(WhereStructure structure) {
        this.structure = structure;
    }

    public String renderWhere(ParameterBind parameterBind) {
        return Optional.of(structure)
                .map(WhereStructure::getCriterion)
                .flatMap(Function.identity())
                .filter(GroupCriterion::hasCriterion)
                .map(groupCriterion -> {
                    CriterionSQLVisitor<SqlAndParameterBind> visitor = new PreparedSQLVisitor(structure.getType(), parameterBind);
                    return groupCriterion.accept(visitor).getSql();
                })
                .orElse(EMPTY);
    }

}
