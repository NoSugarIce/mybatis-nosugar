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

import com.nosugarice.mybatis.support.NameStrategyType;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/9
 */
public class CountMutativeSqlSource extends MutativeSqlSource {

    private static final String DEFAULT_COUNT_METHOD_NAME = "count";
    private static final String COUNT_METHOD_NAME_START = "countWith";

    public CountMutativeSqlSource(Configuration configuration, Class<?> mapperInterface, Method method) {
        super(configuration, mapperInterface, method);
    }

    @Override
    public String defaultQuoteMappedStatementId() {
        String mapperMethodName = method.getName();
        if (DEFAULT_COUNT_METHOD_NAME.equalsIgnoreCase(mapperMethodName)) {
            return "selectList";
        }
        return NameStrategyType.LOWERCASE_FIRST.conversion(
                mapperMethodName.substring(mapperMethodName.indexOf(COUNT_METHOD_NAME_START) + COUNT_METHOD_NAME_START.length()));
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        BoundSql oldBoundSql = defaultMappedStatement.getBoundSql(parameterObject);
        String countStr = "SELECT COUNT(*) " + optimizationCountSql(oldBoundSql.getSql());
        return new BoundSql(configuration, countStr, oldBoundSql.getParameterMappings(), oldBoundSql.getParameterObject());
    }

    /**
     * 优化 Count 语句
     * TODO 先简单粗暴
     *
     * @param sql
     * @return
     */
    public static String optimizationCountSql(String sql) {
        String upperCaseSql = sql.toUpperCase();
        int orderByIndex = upperCaseSql.indexOf("ORDER BY");
        if (orderByIndex > 1) {
            sql = sql.substring(0, orderByIndex);
        }
        return sql.substring(upperCaseSql.indexOf("FROM"));
    }

}
