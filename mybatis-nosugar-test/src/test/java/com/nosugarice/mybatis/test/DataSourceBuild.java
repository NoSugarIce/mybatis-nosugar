package com.nosugarice.mybatis.test;


import com.nosugarice.mybatis.util.StringUtils;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.jdbc.ScriptRunner;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author dingjingyang@foxmail.com
 * @date 2018/7/18
 */
public class DataSourceBuild {

    private String jdbcDriver;
    private String jdbcUrl;
    private String username;
    private String password;
    private final List<String> scriptPaths = new ArrayList<>();
    private final List<String> scripts = new ArrayList<>();

    public DataSource build() {
        DataSource dataSource = new UnpooledDataSource(jdbcDriver, jdbcUrl, username, password);
        runScript(dataSource);
        return dataSource;
    }

    private void runScript(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            ScriptRunner sr = new ScriptRunner(connection);
            sr.setLogWriter(null);
            for (String scriptPath : scriptPaths) {
                InputStream inputStream = getClass().getResourceAsStream(scriptPath);
                if (inputStream == null) {
                    continue;
                }
                sr.runScript(new InputStreamReader(inputStream));
            }
            for (String script : scripts) {
                if (StringUtils.isEmpty(script)) {
                    continue;
                }
                sr.runScript(new StringReader(script));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DataSourceBuild withJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
        return this;
    }

    public DataSourceBuild withJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        return this;
    }

    public DataSourceBuild withUsername(String username) {
        this.username = username;
        return this;
    }

    public DataSourceBuild withPassword(String password) {
        this.password = password;
        return this;
    }

    public DataSourceBuild withScriptPath(String... scriptPaths) {
        this.scriptPaths.addAll(Arrays.asList(scriptPaths));
        return this;
    }

    public DataSourceBuild withScript(String... scripts) {
        this.scripts.addAll(Arrays.asList(scripts));
        return this;
    }

}
