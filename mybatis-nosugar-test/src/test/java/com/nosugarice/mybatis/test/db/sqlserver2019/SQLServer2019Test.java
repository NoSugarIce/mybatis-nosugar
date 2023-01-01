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

package com.nosugarice.mybatis.test.db.sqlserver2019;

import com.nosugarice.mybatis.test.DataSourceBuild;
import com.nosugarice.mybatis.test.db.MapperTest;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2022/1/19
 */
public interface SQLServer2019Test extends MapperTest {

    default Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("mybatis.no-sugar.relational.class-name-to-table-name-strategy", "UPPERCASE_FIRST");
        properties.put("mybatis.no-sugar.relational.field-name-to-column-name-strategy", "ORIGINAL");
        return properties;
    }

    default String[] withScriptPath() {
        return new String[]{
                "/db/sqlserver2019/student_schema.sql"
        };
    }

    default String[] withScript() {
        return new String[0];
    }

    default DataSource getDataSource() {
        return new DataSourceBuild()
                .withJdbcDriver("com.microsoft.sqlserver.jdbc.SQLServerDriver")
                .withJdbcUrl("jdbc:sqlserver://sqlserver2019-service.com:1433;database=Demo;encrypt=false;")
                .withUsername("SA")
                .withPassword("fjfkdjifmlllk987")
                .withScriptPath(withScriptPath())
                .withScript(withScript())
                .build();
    }

}
