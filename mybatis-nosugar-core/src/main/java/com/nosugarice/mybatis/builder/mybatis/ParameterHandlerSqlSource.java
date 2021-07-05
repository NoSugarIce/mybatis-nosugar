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

package com.nosugarice.mybatis.builder.mybatis;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;

/**
 * @author dingjingyang@foxmail.com(dingjingyang)
 * @date 2021/6/12
 */
public abstract class ParameterHandlerSqlSource implements SqlSource {

    private final SqlSource sqlSource;

    public ParameterHandlerSqlSource(SqlSource sqlSource) {
        this.sqlSource = sqlSource;
    }

    /**
     * 更改参数
     *
     * @param parameterObject
     */
    abstract void changeParameter(Object parameterObject);

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        changeParameter(parameterObject);
        return sqlSource.getBoundSql(parameterObject);
    }
}
