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

package com.nosugarice.mybatis.criteria.select;

import com.nosugarice.mybatis.criteria.criterion.JoinCriterion;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public class JoinCriteria {

    private final String masterTableAlias;

    private final List<JoinCriterion<?, ?, ?, ?>> joinCriterionList = new ArrayList<>();

    public JoinCriteria(String masterTableAlias) {
        this.masterTableAlias = masterTableAlias;
    }

    public void addJoinCriterion(JoinCriterion<?, ?, ?, ?> joinCriterion) {
        joinCriterionList.add(joinCriterion);
    }

    public String getMasterTableAlias() {
        return masterTableAlias;
    }

    public List<JoinCriterion<?, ?, ?, ?>> getJoinCriterionList() {
        return joinCriterionList;
    }

}
