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

package com.nosugarice.mybatis.query.process;

import com.nosugarice.mybatis.query.criterion.ColumnCriterion;
import com.nosugarice.mybatis.query.criterion.GroupCriterion;
import com.nosugarice.mybatis.query.criterion.GroupCriterionImpl;
import com.nosugarice.mybatis.sql.Expression;
import com.nosugarice.mybatis.sql.SqlFragment;

import static com.nosugarice.mybatis.sql.SQLConstants.HAVING;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class HavingCriterion extends SqlFragment implements Expression {

    private static final long serialVersionUID = -7975724006388455833L;

    private final GroupCriterion criterion;

    public HavingCriterion(ColumnCriterion<?> criterion) {
        this.criterion = new GroupCriterionImpl(criterion);
    }

    public HavingCriterion(GroupCriterion criterion) {
        this.criterion = criterion;
    }

    @Override
    public String getSql() {
        append(HAVING, criterion.getSql());
        return merge();
    }

}
