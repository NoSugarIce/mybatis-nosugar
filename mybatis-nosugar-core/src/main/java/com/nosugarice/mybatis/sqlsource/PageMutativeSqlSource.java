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

import com.nosugarice.mybatis.dialect.Dialect;
import com.nosugarice.mybatis.domain.Page;
import com.nosugarice.mybatis.mapper.select.SelectPageMapper;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/9
 */
public class PageMutativeSqlSource extends MutativeSqlSource {

    private final Dialect dialect;
    private final MappedStatement originalMappedStatement;

    public PageMutativeSqlSource(Configuration configuration, Dialect dialect, Class<?> mapperInterface, Method method) {
        super(configuration, mapperInterface, method);
        this.dialect = dialect;
        this.originalMappedStatement = copyMappedStatement(getDefaultSelectMappedStatement());
    }

    @Override
    public String defaultQuoteMappedStatementId() {
        return methodName;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        BoundSql originalBoundSql = originalMappedStatement.getBoundSql(parameterObject);
        Page<?> page = SelectPageMapper.PageStorage.getPage();
        if (page == null) {
            return originalBoundSql;
        }
        String pageSql = dialect.getLimitHandler().processSql(originalBoundSql.getSql(), page.getOffset(), page.getLimit());
        return createNewBoundSql(pageSql, originalBoundSql);
    }

}
