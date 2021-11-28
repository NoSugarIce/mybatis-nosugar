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

import java.util.Iterator;
import java.util.StringJoiner;

import static com.nosugarice.mybatis.sql.SQLConstants.AND;
import static com.nosugarice.mybatis.sql.SQLConstants.EMPTY;
import static com.nosugarice.mybatis.sql.SQLConstants.SPACE;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/25
 */
public class PlaceholderRenderingContext extends AbstractRenderingContext {

    private final String prefix;

    public PlaceholderRenderingContext(Class<?> entityClass, Iterator<String> paramNameIterator, String prefix) {
        super(entityClass, paramNameIterator);
        this.prefix = prefix;
    }

    @Override
    public String getSingleValuePlaceholder(String column) {
        if (paramNameIterator.hasNext()) {
            String paramName = paramNameIterator.next();
            paramNameIterator.remove();
            return getPlaceholder(column, paramName, prefix, null, null);
        }
        return EMPTY;
    }

    @Override
    public String getTwoValuePlaceholder(String column) {
        return getSingleValuePlaceholder(column) + SPACE + AND + SPACE + getSingleValuePlaceholder(column);
    }

    @Override
    public String getListValuePlaceholder(String column) {
        StringJoiner stringJoiner = new StringJoiner(", ", "(", ")");
        while (paramNameIterator.hasNext()) {
            String paramName = paramNameIterator.next();
            paramNameIterator.remove();
            stringJoiner.add(getPlaceholder(column, paramName, prefix, null, null));
        }
        return stringJoiner.toString();
    }
}
