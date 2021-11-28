package com.nosugarice.mybatis.query.jpa;

import com.nosugarice.mybatis.query.criterion.AnywhereLike;
import com.nosugarice.mybatis.query.criterion.Between;
import com.nosugarice.mybatis.query.criterion.ColumnCriterion;
import com.nosugarice.mybatis.query.criterion.Empty;
import com.nosugarice.mybatis.query.criterion.EndLike;
import com.nosugarice.mybatis.query.criterion.Equal;
import com.nosugarice.mybatis.query.criterion.ExactLike;
import com.nosugarice.mybatis.query.criterion.GreaterThan;
import com.nosugarice.mybatis.query.criterion.GreaterThanOrEqual;
import com.nosugarice.mybatis.query.criterion.In;
import com.nosugarice.mybatis.query.criterion.LessThan;
import com.nosugarice.mybatis.query.criterion.LessThanOrEqual;
import com.nosugarice.mybatis.query.criterion.NotAnywhereLike;
import com.nosugarice.mybatis.query.criterion.NotEmpty;
import com.nosugarice.mybatis.query.criterion.NotEqual;
import com.nosugarice.mybatis.query.criterion.NotExactLike;
import com.nosugarice.mybatis.query.criterion.NotNull;
import com.nosugarice.mybatis.query.criterion.Null;
import com.nosugarice.mybatis.query.criterion.StartLike;

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

    IS_NOT_NULL(ValueType.NO, new String[]{"IsNotNull", "NotNull"}, NotNull.class),
    IS_NULL(ValueType.NO, new String[]{"IsNull", "Null"}, Null.class),
    NOT_EQUALS(ValueType.SINGLE, new String[]{"Not", "NotEquals"}, NotEqual.class),
    EQUALS(ValueType.SINGLE, new String[]{"Equals", "Is"}, Equal.class),
    GREATER_THAN(ValueType.SINGLE, new String[]{"IsGreaterThan", "GreaterThan"}, GreaterThan.class),
    GREATER_THAN_EQUAL(ValueType.SINGLE, new String[]{"IsGreaterThanEqual", "GreaterThanEqual"}, GreaterThanOrEqual.class),
    LESS_THAN(ValueType.SINGLE, new String[]{"IsLessThan", "LessThan"}, LessThan.class),
    LESS_THAN_EQUAL(ValueType.SINGLE, new String[]{"IsLessThanEqual", "LessThanEqual"}, LessThanOrEqual.class),
    NOT_IN(ValueType.COLLECTION, new String[]{"IsNotIn", "NotIn"}, In.class),
    IN(ValueType.COLLECTION, new String[]{"IsIn", "In"}, In.class),
    BETWEEN(ValueType.TWO, new String[]{"IsBetween", "Between"}, Between.class),
    NOT_LIKE(ValueType.SINGLE, new String[]{"IsNotLike", "NotLike"}, NotExactLike.class),
    LIKE(ValueType.SINGLE, new String[]{"IsLike", "Like"}, ExactLike.class),
    STARTING_WITH(ValueType.SINGLE, new String[]{"IsStartingWith", "StartingWith", "StartsWith"}, StartLike.class),
    ENDING_WITH(ValueType.SINGLE, new String[]{"IsEndingWith", "EndingWith", "EndsWith"}, EndLike.class),
    NOT_CONTAINING(ValueType.SINGLE, new String[]{"IsNotContaining", "NotContaining", "NotContains"}, NotAnywhereLike.class),
    CONTAINING(ValueType.SINGLE, new String[]{"IsContaining", "Containing", "Contains"}, AnywhereLike.class),
    IS_NOT_EMPTY(ValueType.NO, new String[]{"IsNotEmpty", "NotEmpty"}, NotEmpty.class),
    IS_EMPTY(ValueType.NO, new String[]{"IsEmpty", "Empty"}, Empty.class),
    BEFORE(ValueType.SINGLE, new String[]{"IsBefore", "Before"}, LessThan.class),
    AFTER(ValueType.SINGLE, new String[]{"IsAfter", "After"}, GreaterThan.class),
    ;

    public static Set<String> allKeywords = new HashSet<>();

    static {
        for (ConditionType conditionType : values()) {
            allKeywords.addAll(conditionType.keywords);
        }
    }

    private final ValueType valueType;
    private final Set<String> keywords;
    private final Class<? extends ColumnCriterion<?>> propertyCriterionTye;

    <T extends ColumnCriterion<?>> ConditionType(ValueType valueType, String[] keywords, Class<T> propertyCriterionClass) {
        this.valueType = valueType;
        this.keywords = new HashSet<>(Arrays.asList(keywords));
        this.propertyCriterionTye = propertyCriterionClass;
    }

    protected boolean supports(String property) {
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
                return property.replaceAll(keyword, "");
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

    public ValueType getValueType() {
        return valueType;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public Class<? extends ColumnCriterion<?>> getPropertyCriterionTye() {
        return propertyCriterionTye;
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
