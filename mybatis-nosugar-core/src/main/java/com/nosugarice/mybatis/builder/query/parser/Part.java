package com.nosugarice.mybatis.builder.query.parser;

import com.nosugarice.mybatis.builder.query.ConditionType;

/**
 * org.springframework.data.repository.query.parser.Part
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class Part {

    private final ConditionType conditionType;
    private final String propertyName;

    public Part(String source) {
        this.conditionType = ConditionType.fromProperty(source);
        this.propertyName = conditionType.getPropertyName(source);
    }

    public int getNumberOfArguments() {
        return conditionType.getValueType().getNumberOfParameters();
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public String getPropertyName() {
        return propertyName;
    }

}
