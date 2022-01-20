package com.nosugarice.mybatis.test.db.postgresql;

import com.nosugarice.mybatis.test.DataSourceBuild;
import com.nosugarice.mybatis.test.db.MapperTest;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2022/1/19
 */
public interface PostgreSqlTest extends MapperTest {

    default Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("mybatis.no-sugar.relational.class-name-to-table-name-strategy", "CAMEL_TO_UNDERSCORE");
        properties.put("mybatis.no-sugar.relational.field-name-to-column-name-strategy", "CAMEL_TO_UNDERSCORE");
        return properties;
    }

    default String[] withScriptPath() {
        return new String[]{
                "/db/postgresql/student_schema.sql"
        };
    }

    default String[] withScript() {
        return new String[0];
    }

    default DataSource getDataSource() {
        return new DataSourceBuild()
                .withJdbcDriver("org.postgresql.Driver")
                .withJdbcUrl("jdbc:postgresql://postgresql-service.com:5432/demo")
                .withUsername("postgres")
                .withPassword("fjfkdjifmlllk987")
                .withScriptPath(withScriptPath())
                .withScript(withScript())
                .build();
    }

}
