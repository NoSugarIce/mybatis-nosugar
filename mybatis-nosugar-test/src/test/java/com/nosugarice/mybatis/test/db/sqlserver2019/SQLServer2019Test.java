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
