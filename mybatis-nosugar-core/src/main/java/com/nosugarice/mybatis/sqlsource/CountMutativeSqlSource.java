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

package com.nosugarice.mybatis.sqlsource;

import com.nosugarice.mybatis.config.internal.NameStrategyType;
import com.nosugarice.mybatis.sql.SQLConstants;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/9
 */
public class CountMutativeSqlSource extends MutativeSqlSource {

    public static final String DEFAULT_COUNT_METHOD = "count";
    public static final String COUNT_METHOD_START = "countWith";

    public CountMutativeSqlSource(Configuration configuration, Class<?> mapperInterface, Method method) {
        super(configuration, mapperInterface, method);
    }

    @Override
    public String defaultQuoteMappedStatementId() {
        if (DEFAULT_COUNT_METHOD.equalsIgnoreCase(methodName)) {
            return "selectList";
        }
        return NameStrategyType.LOWERCASE_FIRST.conversion(
                methodName.substring(methodName.indexOf(COUNT_METHOD_START) + COUNT_METHOD_START.length()));
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        BoundSql originalBoundSql = defaultMappedStatement.getBoundSql(parameterObject);
        String countStr = "SELECT COUNT(*) " + optimizationCountSql(originalBoundSql.getSql());
        return createNewBoundSql(countStr, originalBoundSql);
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
        int orderByIndex = upperCaseSql.indexOf(SQLConstants.ORDER_BY);
        if (orderByIndex > 1) {
            sql = sql.substring(0, orderByIndex);
        }
        return sql.substring(upperCaseSql.indexOf(SQLConstants.FROM));
    }

}