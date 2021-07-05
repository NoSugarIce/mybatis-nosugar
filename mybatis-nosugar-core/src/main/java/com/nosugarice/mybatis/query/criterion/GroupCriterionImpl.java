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

package com.nosugarice.mybatis.query.criterion;

import com.nosugarice.mybatis.sql.Expression;
import com.nosugarice.mybatis.sql.criterion.GroupCriterion;
import com.nosugarice.mybatis.sql.criterion.PropertyCriterion;
import com.nosugarice.mybatis.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class GroupCriterionImpl extends AbstractCriterion<GroupCriterionImpl> implements GroupCriterion {

    private static final long serialVersionUID = -6979194990435135901L;

    private final List<PropertyCriterion<?>> propertyCriterions = new ArrayList<>();

    public GroupCriterionImpl(Separator separator) {
        this.separator = separator;
    }

    public GroupCriterionImpl(PropertyCriterion<?>... criterions) {
        this.propertyCriterions.addAll(Stream.of(criterions).collect(Collectors.toList()));
    }

    public GroupCriterionImpl(Separator separator, PropertyCriterion<?>... criterions) {
        this.separator = separator;
        this.propertyCriterions.addAll(Stream.of(criterions).collect(Collectors.toList()));
    }

    public GroupCriterionImpl(Collection<PropertyCriterion<?>> criterions) {
        this.propertyCriterions.addAll(criterions);
    }

    public GroupCriterionImpl(Separator separator, Collection<PropertyCriterion<?>> criterions) {
        this.separator = separator;
        this.propertyCriterions.addAll(criterions);
    }

    @Override
    public final GroupCriterionImpl append(PropertyCriterion<?>... criterions) {
        this.propertyCriterions.addAll(Stream.of(criterions).collect(Collectors.toList()));
        return this;
    }

    public GroupCriterionImpl append(Collection<PropertyCriterion<?>> criterions) {
        this.propertyCriterions.addAll(criterions);
        return this;
    }

    @Override
    public Separator getSeparator() {
        return separator == null ? Separator.OR : separator;
    }

    @Override
    public String getSql() {
        if (propertyCriterions.isEmpty()) {
            return "";
        }
        String criterionSql = propertyCriterions.stream()
                .map(Expression::getSql)
                .collect(Collectors.joining());

        append(getSeparator().name(), "(", StringUtils.trim(criterionSql
                , Arrays.asList(Separator.AND.name(), Separator.OR.name()), null), ")");
        return merge();
    }

    @Override
    public List<PropertyCriterion<?>> getCriterions() {
        return propertyCriterions;
    }
}
