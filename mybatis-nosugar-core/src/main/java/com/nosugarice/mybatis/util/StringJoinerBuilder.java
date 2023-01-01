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

package com.nosugarice.mybatis.util;

import java.util.StringJoiner;

/**
 * java.util.StringJoiner
 *
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public class StringJoinerBuilder {

    private String delimiter;
    private String prefix;
    private String suffix;

    private String[] valueArr = new String[512];

    private int index = 0;

    public static StringJoinerBuilder createSpaceJoin() {
        return new StringJoinerBuilder().withDelimiter(" ");
    }

    public StringJoinerBuilder withDelimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public StringJoinerBuilder withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public StringJoinerBuilder withSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public StringJoinerBuilder withElements(boolean condition, String... elements) {
        if (condition) {
            withElements(elements);
        }
        return this;
    }

    public StringJoinerBuilder withElements(String... elements) {
        for (String element : elements) {
            if (element != null) {
                if (index == valueArr.length) {
                    String[] newValueArr = new String[index + 265];
                    System.arraycopy(valueArr, 0, newValueArr, 0, valueArr.length);
                    valueArr = newValueArr;
                }
                valueArr[index] = element;
                index++;
            }
        }
        return this;
    }

    public String build() {
        if (delimiter == null) {
            this.delimiter = "";
        }
        if (prefix == null) {
            this.prefix = "";
        }
        if (suffix == null) {
            this.suffix = "";
        }
        StringJoiner stringJoiner = new StringJoiner(delimiter, prefix, suffix);
        for (int i = 0; i < index; i++) {
            stringJoiner.add(valueArr[i]);
        }
        return stringJoiner.toString();
    }

}
