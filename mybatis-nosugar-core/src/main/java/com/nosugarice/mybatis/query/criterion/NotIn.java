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

import java.util.Collection;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class NotIn<T> extends In<T> {

    private static final long serialVersionUID = 3538035978096818449L;

    public NotIn(String column) {
        super(column);
    }

    public NotIn(String column, T[] values) {
        super(column, values);
    }

    public NotIn(String column, T[] values, Separator separator) {
        super(column, values, separator);
    }

    public NotIn(String column, Collection<T> values) {
        super(column, values);
    }

    public NotIn(String column, Collection<T> values, Separator separator) {
        super(column, values, separator);
    }

    @Override
    public boolean isNegated() {
        return !super.isNegated();
    }

}
