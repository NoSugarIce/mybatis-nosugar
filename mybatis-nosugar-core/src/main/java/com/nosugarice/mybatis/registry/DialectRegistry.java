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

package com.nosugarice.mybatis.registry;

import com.nosugarice.mybatis.dialect.DB2Dialect;
import com.nosugarice.mybatis.dialect.Dialect;
import com.nosugarice.mybatis.dialect.HSQLDialect;
import com.nosugarice.mybatis.dialect.MySqlDialect;
import com.nosugarice.mybatis.dialect.OracleDialect;
import com.nosugarice.mybatis.dialect.PostgreSqlDialect;
import com.nosugarice.mybatis.dialect.SqlServer2012Dialect;
import com.nosugarice.mybatis.dialect.SqlServerDialect;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/6/12
 */
public class DialectRegistry extends BeanRegistry<Dialect> {

    public DialectRegistry() {
        registerType("MYSQL", MySqlDialect.class);
        registerType("ORACLE", OracleDialect.class);
        registerType("SQLSERVER", SqlServerDialect.class);
        registerType("SQLSERVER2012", SqlServer2012Dialect.class);
        registerType("POSTGRESQL", PostgreSqlDialect.class);
        registerType("DB2", DB2Dialect.class);
        registerType("HSQL DATABASE ENGINE", HSQLDialect.class);
    }
}
