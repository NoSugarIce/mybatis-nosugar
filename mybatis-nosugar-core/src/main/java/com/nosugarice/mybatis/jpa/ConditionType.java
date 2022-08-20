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

package com.nosugarice.mybatis.jpa;

import com.nosugarice.mybatis.criteria.criterion.ColumnCriterion;
import com.nosugarice.mybatis.criteria.where.criterion.Between;
import com.nosugarice.mybatis.criteria.where.criterion.Empty;
import com.nosugarice.mybatis.criteria.where.criterion.EqualTo;
import com.nosugarice.mybatis.criteria.where.criterion.GreaterThan;
import com.nosugarice.mybatis.criteria.where.criterion.GreaterThanOrEqual;
import com.nosugarice.mybatis.criteria.where.criterion.In;
import com.nosugarice.mybatis.criteria.where.criterion.IsNull;
import com.nosugarice.mybatis.criteria.where.criterion.LessThan;
import com.nosugarice.mybatis.criteria.where.criterion.LessThanOrEqual;
import com.nosugarice.mybatis.criteria.where.criterion.Like;
import com.nosugarice.mybatis.exception.NoSugarException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * org.springframework.data.repository.query.parser.Part.Type
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/12/6
 */
public enum ConditionType {

    IS_NOT_NULL(ValueType.NO, new String[]{"IsNotNull", "NotNull"}, IsNull.class, true),
    IS_NULL(ValueType.NO, new String[]{"IsNull", "Null"}, IsNull.class, false),
    NOT_EQUALS(ValueType.SINGLE, new String[]{"Not", "NotEquals"}, EqualTo.class, true),
    EQUALS(ValueType.SINGLE, new String[]{"Equals", "Is"}, EqualTo.class, false),
    GREATER_THAN(ValueType.SINGLE, new String[]{"IsGreaterThan", "GreaterThan"}, GreaterThan.class, false),
    GREATER_THAN_EQUAL(ValueType.SINGLE, new String[]{"IsGreaterThanEqual", "GreaterThanEqual"}, GreaterThanOrEqual.class, false),
    LESS_THAN(ValueType.SINGLE, new String[]{"IsLessThan", "LessThan"}, LessThan.class, false),
    LESS_THAN_EQUAL(ValueType.SINGLE, new String[]{"IsLessThanEqual", "LessThanEqual"}, LessThanOrEqual.class, false),
    NOT_IN(ValueType.COLLECTION, new String[]{"IsNotIn", "NotIn"}, In.class, false),
    IN(ValueType.COLLECTION, new String[]{"IsIn", "In"}, In.class, false),
    BETWEEN(ValueType.TWO, new String[]{"IsBetween", "Between"}, Between.class, false),
    NOT_LIKE(ValueType.SINGLE, new String[]{"IsNotLike", "NotLike"}, Like.class, true),
    LIKE(ValueType.SINGLE, new String[]{"IsLike", "Like"}, Like.class, false),
    STARTING_WITH(ValueType.SINGLE, new String[]{"IsStartingWith", "StartingWith", "StartsWith"}, Like.StartLike.class, false),
    ENDING_WITH(ValueType.SINGLE, new String[]{"IsEndingWith", "EndingWith", "EndsWith"}, Like.EndLike.class, false),
    NOT_CONTAINING(ValueType.SINGLE, new String[]{"IsNotContaining", "NotContaining", "NotContains"}, Like.AnyLike.class, true),
    CONTAINING(ValueType.SINGLE, new String[]{"IsContaining", "Containing", "Contains"}, Like.AnyLike.class, false),
    IS_NOT_EMPTY(ValueType.NO, new String[]{"IsNotEmpty", "NotEmpty"}, Empty.class, true),
    IS_EMPTY(ValueType.NO, new String[]{"IsEmpty", "Empty"}, Empty.class, false),
    BEFORE(ValueType.SINGLE, new String[]{"IsBefore", "Before"}, LessThan.class, false),
    AFTER(ValueType.SINGLE, new String[]{"IsAfter", "After"}, GreaterThan.class, false),
    ;

    private final ValueType valueType;
    private final Set<String> keywords;
    private final Class<? extends ColumnCriterion<?>> columnCriterionType;
    private final boolean negated;

    <T extends ColumnCriterion<?>> ConditionType(ValueType valueType, String[] keywords, Class<T> columnCriterionType, boolean negated) {
        this.valueType = valueType;
        this.keywords = new HashSet<>(Arrays.asList(keywords));
        this.columnCriterionType = columnCriterionType;
        this.negated = negated;
    }

    boolean supports(String property) {
        for (String keyword : keywords) {
            if (property.endsWith(keyword)) {
                return true;
            }
        }
        return false;
    }

    public String getPropertyName(String property) {
        for (String keyword : keywords) {
            if (property.endsWith(keyword)) {
                return property.replace(keyword, "");
            }
        }
        return property;
    }

    public static ConditionType fromProperty(String rawProperty) {
        for (ConditionType type : values()) {
            if (type.supports(rawProperty)) {
                return type;
            }
        }
        return EQUALS;
    }

    public ColumnCriterion<?> createColumnCriterion(String column) {
        try {
            ColumnCriterion<?> columnCriterion = this.columnCriterionType.getConstructor(String.class).newInstance(column);
            if (this.negated) {
                columnCriterion.not();
            }
            return columnCriterion;
        } catch (Exception e) {
            throw new NoSugarException(e);
        }
    }

    public ValueType getValueType() {
        return valueType;
    }

    public enum ValueType {
        NO(0),
        SINGLE(1),
        TWO(2),
        COLLECTION(1);

        private final int numberOfParameters;

        ValueType(int numberOfParameters) {
            this.numberOfParameters = numberOfParameters;
        }

        public int getNumberOfParameters() {
            return numberOfParameters;
        }
    }

}
