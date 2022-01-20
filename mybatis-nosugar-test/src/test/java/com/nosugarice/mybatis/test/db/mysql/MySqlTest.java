package com.nosugarice.mybatis.test.db.mysql;

import com.nosugarice.mybatis.test.DataSourceBuild;
import com.nosugarice.mybatis.test.db.MapperTest;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2022/1/19
 */
public interface MySqlTest extends MapperTest {

    default Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("mybatis.no-sugar.relational.class-name-to-table-name-strategy", "CAMEL_TO_UNDERSCORE");
        properties.put("mybatis.no-sugar.relational.field-name-to-column-name-strategy", "CAMEL_TO_UNDERSCORE");
        return properties;
    }

    default String[] withScriptPath() {
        return new String[]{
                "/db/mysql/student_schema.sql"
        };
    }

    default String[] withScript() {
        return new String[0];
    }

    default DataSource getDataSource() {
        return new DataSourceBuild()
                .withJdbcDriver("com.mysql.cj.jdbc.Driver")
                .withJdbcUrl("jdbc:mysql://mysql-service.com:3306/demo?zeroDateTimeBehavior=convertToNull&rewriteBatchedStatements=true")
                .withUsername("root")
                .withPassword("fjfkdjifmlllk987")
                .withScriptPath(withScriptPath())
                .withScript(withScript())
                .build();
    }

}
