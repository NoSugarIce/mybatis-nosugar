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

package com.nosugarice.mybatis.builder.statement;

import com.nosugarice.mybatis.config.DmlType;

import java.lang.reflect.Method;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/2
 */
public class UpdateStatementBuilder extends StatementBuilder {

    UpdateStatementBuilder() {
    }

    @Override
    public DmlType getDmlType(Method method) {
        return DmlType.UPDATE;
    }

}
