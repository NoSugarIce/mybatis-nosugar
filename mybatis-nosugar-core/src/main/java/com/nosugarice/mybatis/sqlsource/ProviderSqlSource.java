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

import com.nosugarice.mybatis.annotation.Provider;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/9
 */
public abstract class ProviderSqlSource implements SqlSource {

    protected final Configuration configuration;
    protected final Class<?> mapperInterface;
    protected final Method method;
    protected final MappedStatement defaultMappedStatement;

    protected ProviderSqlSource(Configuration configuration, Class<?> mapperInterface, Method method) {
        this.configuration = configuration;
        this.mapperInterface = mapperInterface;
        this.method = method;
        this.defaultMappedStatement = getDefaultSelectMappedStatement();
    }

    /**
     * 默认使用的基础MappedStatement
     *
     * @return
     */
    public String defaultQuoteMappedStatementId() {
        return Optional.of(method).map(method1 -> method1.getAnnotation(Provider.class)).map(Provider::originalMethod).orElse("");
    }

    /**
     * 获取默认引用查询方法
     *
     * @return
     */
    public MappedStatement getDefaultSelectMappedStatement() {
        String mappedStatementId = mapperInterface.getName() + "." + defaultQuoteMappedStatementId();
        return getSelectMappedStatement(mappedStatementId);
    }

    /**
     * 获取指定引用查询方法
     *
     * @param mappedStatementId
     * @return
     */
    public MappedStatement getSelectMappedStatement(String mappedStatementId) {
        MappedStatement mappedStatement = configuration.getMappedStatement(mappedStatementId);
        if (mappedStatement == null) {
            throw new BuilderException("没有找到运行时默认查询!");
        }
        return mappedStatement;
    }

    public static BoundSql createNewBoundSql(Configuration configuration, String sql, BoundSql originalBoundSql) {
        BoundSql boundSql = new BoundSql(configuration, sql, originalBoundSql.getParameterMappings(), originalBoundSql.getParameterObject());
        for (ParameterMapping parameterMapping : originalBoundSql.getParameterMappings()) {
            Object parameter = originalBoundSql.getAdditionalParameter(parameterMapping.getProperty());
            if (parameter == null && !boundSql.hasAdditionalParameter(parameterMapping.getProperty())) {
                continue;
            }
            boundSql.setAdditionalParameter(parameterMapping.getProperty(), parameter);
        }
        return boundSql;
    }

}
