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

package com.nosugarice.mybatis.sql;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/1/1
 */
public interface Placeholder {

    String TABLE_P = "${TABLE}";
    String FROM_TABLE_P = "${FROM_TABLE}";
    String TABLE_ALIAS_P = "${TABLE_ALIAS}";
    String AS_ALIAS_P = "${AS_ALIAS}";
    String FROM_WITH_ALIAS_P = "${FROM_WITH_ALIAS}";
    String ALIAS_STATE_P = "${ALIAS_STATE}";
    String SUB_QUERY = "${SUB_QUERY}";

    /**
     * 表别名占位符+列
     *
     * @param column
     * @return
     */
    static String columnAliasState(String column) {
        return ALIAS_STATE_P + column;
    }

}
