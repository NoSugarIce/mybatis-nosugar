package com.nosugarice.mybatis.test.db;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2022/1/19
 */
public interface MapperTest {

    Map<String, String> getProperties();

    String[] withScriptPath();

    String[] withScript();

    DataSource getDataSource();

}
