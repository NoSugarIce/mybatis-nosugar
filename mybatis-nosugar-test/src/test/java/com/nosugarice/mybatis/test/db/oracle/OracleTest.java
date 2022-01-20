package com.nosugarice.mybatis.test.db.oracle;

import com.nosugarice.mybatis.test.DataSourceBuild;
import com.nosugarice.mybatis.test.db.MapperTest;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2022/1/19
 */
public interface OracleTest extends MapperTest {

    default Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("mybatis.no-sugar.relational.class-name-to-table-name-strategy", "CAMEL_TO_UNDERSCORE_UPPERCASE");
        properties.put("mybatis.no-sugar.relational.field-name-to-column-name-strategy", "CAMEL_TO_UNDERSCORE_UPPERCASE");
        return properties;
    }

    default String[] withScriptPath() {
        return new String[]{
                "/db/oracle/student_schema.sql"
        };
    }

    default String[] withScript() {
        return new String[0];
    }

    default DataSource getDataSource() {
        return new DataSourceBuild()
                .withJdbcDriver("oracle.jdbc.OracleDriver")
                .withJdbcUrl("jdbc:oracle:thin:@oracle-service.com:1521:helowin")
                .withUsername("demo")
                .withPassword("demo930393")
                .withScriptPath(withScriptPath())
                .withScript(withScript())
                .build();
    }

}
