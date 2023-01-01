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

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/1/9
 */
public class StringFormatter {

    private static final String PLACEHOLDER = "{}";

    private static final String PLACEHOLDER_PREFIX = "${";
    private static final String PLACEHOLDER_SUFFIX = "}";

    public static String format(String format, Object... arguments) {
        if (arguments == null) {
            return format;
        }
        StringBuilder stringBuilder = new StringBuilder(format);
        Iterator<Object> iterator = Arrays.stream(arguments).iterator();
        int lastIndexOf = 0;
        while (iterator.hasNext()) {
            int indexOf = stringBuilder.indexOf(PLACEHOLDER, lastIndexOf);
            if (indexOf < 0) {
                throw new IllegalArgumentException("占位符与参数数目不符!");
            }
            lastIndexOf = indexOf;
            Object next = iterator.next();
            stringBuilder.replace(indexOf, indexOf + 2, String.valueOf(next));
        }
        int indexOf = stringBuilder.indexOf(PLACEHOLDER);
        if (indexOf > 0) {
            throw new IllegalArgumentException("占位符与参数数目不符!");
        }
        return stringBuilder.toString();
    }

    public static String replacePlaceholder(String format, Map<String, String> placeholderValues) {
        if (format == null) {
            return null;
        }
        if (placeholderValues == null || placeholderValues.isEmpty()) {
            return format;
        }
        StringBuilder stringBuilder = new StringBuilder(format);
        int index = stringBuilder.indexOf(PLACEHOLDER_PREFIX);
        while (index > -1) {
            int end = stringBuilder.indexOf(PLACEHOLDER_SUFFIX, index);
            String placeholder = stringBuilder.substring(index + PLACEHOLDER_PREFIX.length(), end);
            String value = placeholderValues.get(placeholder);
            if (value == null) {
                value = placeholderValues.get(PLACEHOLDER_PREFIX + placeholder + PLACEHOLDER_SUFFIX);
            }
            if (value != null) {
                stringBuilder.replace(index, end + PLACEHOLDER_SUFFIX.length(), value);
            }

            index = stringBuilder.indexOf(PLACEHOLDER_PREFIX, value == null ? end : index + value.length());
        }
        return stringBuilder.toString();
    }

}
