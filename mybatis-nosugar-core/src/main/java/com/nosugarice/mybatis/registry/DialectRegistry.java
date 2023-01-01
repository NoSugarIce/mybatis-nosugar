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

package com.nosugarice.mybatis.registry;

import com.nosugarice.mybatis.dialect.DB2Dialect;
import com.nosugarice.mybatis.dialect.Dialect;
import com.nosugarice.mybatis.dialect.HSQLDialect;
import com.nosugarice.mybatis.dialect.MySqlDialect;
import com.nosugarice.mybatis.dialect.OracleDialect;
import com.nosugarice.mybatis.dialect.PostgreSqlDialect;
import com.nosugarice.mybatis.dialect.SqlServer2012Dialect;
import com.nosugarice.mybatis.dialect.SqlServerDialect;
import com.nosugarice.mybatis.util.Preconditions;
import com.nosugarice.mybatis.util.ReflectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/6/12
 */
public class DialectRegistry extends BeanRegistry<Dialect> {

    private final Map<String, List<Class<? extends Dialect>>> differentVersionMap;

    public DialectRegistry() {
        registerType("MYSQL", MySqlDialect.class);
        registerType("ORACLE", OracleDialect.class);
        registerType("POSTGRESQL", PostgreSqlDialect.class);
        registerType("DB2", DB2Dialect.class);
        registerType("HSQL DATABASE ENGINE", HSQLDialect.class);

        differentVersionMap = new HashMap<>();
        differentVersionMap.put("MICROSOFT SQL SERVER", Arrays.asList(SqlServer2012Dialect.class, SqlServerDialect.class));
    }

    public Dialect chooseDialect(String databaseName, int version) {
        Dialect dialect = containsType(databaseName) ? getObject(databaseName) : null;
        if (dialect == null) {
            List<Class<? extends Dialect>> dialectClasses = differentVersionMap.get(databaseName.toUpperCase());
            if (dialectClasses != null) {
                for (Class<? extends Dialect> dialectClass : dialectClasses) {
                    Dialect dialectTemp = ReflectionUtils.newInstance(dialectClass);
                    if (dialectTemp.supportsVersion(version)) {
                        return dialectTemp;
                    }
                }
            }
        }
        Preconditions.checkNotNull(dialect, "No dialect found for database '" + databaseName + "'");
        return dialect;
    }

}
