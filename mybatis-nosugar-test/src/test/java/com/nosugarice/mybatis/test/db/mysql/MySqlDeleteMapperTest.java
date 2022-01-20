package com.nosugarice.mybatis.test.db.mysql;

import com.nosugarice.mybatis.test.db.DeleteMapperTest;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/20
 */
class MySqlDeleteMapperTest extends DeleteMapperTest implements MySqlTest {

    @Override
    public String[] withScript() {
        return new String[]{
                "INSERT INTO student VALUES ('002f2dcb10ba4be0adc333cecb886111', '张三', 12, 1, '10021', '18600509022', '南京', 0.00, 0, 0,'2021-07-03 14:46:23', NULL, NULL);",
                "INSERT INTO student VALUES ('002f2dcb10ba4be0adc333cecb896118', '李四', 20, 1, '10022', '18600509025', '驻马店', 100.00, 0, 0,'2021-07-03 14:46:23', NULL, NULL);",
                "INSERT INTO student VALUES ('002f2dcb10ba4be0adc333cecb896925', '王五', 25, 1, '10023', '18600509026', '白鹿原', 0.00, 1, 0,'2021-07-03 14:46:23', NULL, NULL);"
        };
    }

}