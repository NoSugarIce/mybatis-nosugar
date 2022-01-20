package com.nosugarice.mybatis.test.db.postgresql;

import com.nosugarice.mybatis.test.db.UpdateMapperTest;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/20
 */
class PostgreSqlUpdateMapperTest extends UpdateMapperTest implements PostgreSqlTest {

    @Override
    public String[] withScript() {
        return new String[]{
                "INSERT INTO student VALUES ('002f2dcb10ba4be0adc333cecb886111', '张三', 12, 1, '10021', '18600509022', '南京', 0.00, 0, 0,'2021-07-03 14:46:23', NULL, NULL);"
        };
    }

}