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

package com.nosugarice.mybatis.jpa.parser;

import com.nosugarice.mybatis.jpa.ConditionType;

/**
 * org.springframework.data.repository.query.parser.Part
 *
 * @author spring.data
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
