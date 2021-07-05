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

package com.nosugarice.mybatis.query;

import java.util.StringJoiner;

/**
 * 仅只能进行一次merge,如果重新赋值先调用clean
 *
 * @author dingjingyang@foxmail.com
 * @date 2021/3/5
 */
public abstract class SqlFragment {

    private StringJoiner sqlFragments;

    private boolean isMerge = false;

    public SqlFragment append(String... fragments) {
        if (isMerge) {
            return this;
        }
        if (sqlFragments == null) {
            sqlFragments = creatSqlFragments();
        }
        for (String fragment : fragments) {
            sqlFragments.add(fragment);
        }
        return this;
    }

    public String merge() {
        isMerge = true;
        return sqlFragments.toString();
    }

    public SqlFragment clean() {
        sqlFragments = creatSqlFragments();
        isMerge = false;
        return this;
    }

    private StringJoiner creatSqlFragments() {
        return new StringJoiner(" ", " ", " ");
    }

}
