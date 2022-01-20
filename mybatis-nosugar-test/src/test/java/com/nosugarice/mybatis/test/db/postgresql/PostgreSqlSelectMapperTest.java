package com.nosugarice.mybatis.test.db.postgresql;

import com.nosugarice.mybatis.test.db.SelectMapperTest;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/20
 */
class PostgreSqlSelectMapperTest extends SelectMapperTest implements PostgreSqlTest {

    @Override
    public String[] withScript() {
        return new String[]{
                "INSERT INTO student VALUES ('002f2dcb10ba4be0adc333cecb886111', '张三', 12, 1, '10021', '18600509022', '南京', 0.00, 0, 0,'2021-07-03 14:46:23', NULL, NULL);",
                "INSERT INTO student VALUES ('002f2dcb10ba4be0adc333cecb896118', '李四', 20, 1, '10022', '18600509025', '驻马店', 100.00, 0, 0,'2021-07-03 14:46:23', NULL, NULL);",
                "INSERT INTO student VALUES ('002f2dcb10ba4be0adc333cecbffdd25', '张五', 25, 1, '10023', '18600509021', '白鹿原', 0.00, 1, 0,'2021-07-03 14:46:23', NULL, NULL);",
                "INSERT INTO student VALUES ('002f2dcb10ba4be0adc3fgfgfb896925', '刘五', 25, 1, '10023', '18600509022', '白鹿原', 0.00, 1, 0,'2021-07-03 14:46:23', NULL, NULL);",
                "INSERT INTO student VALUES ('002f2dcb10ba4be0adcegssecb896925', '王五', 25, 1, '10023', '18600509023', '白鹿原', 0.00, 1, 0,'2021-07-03 14:46:23', NULL, NULL);",
                "INSERT INTO student VALUES ('002f2dcb10bass4be0ssadcecb896925', '柏五', 25, 1, '10023', '18600509024', '白鹿原', 0.00, 1, 0,'2021-07-03 14:46:23', NULL, NULL);",
                "INSERT INTO student VALUES ('002f2dcb10bassdf4be0adcecb896925', '白五', 25, 1, '10023', '18600509025', '白鹿原', 0.00, 1, 0,'2021-07-03 14:46:23', NULL, NULL);",
                "INSERT INTO student VALUES ('002f2dcb10ba4be0fsfsadcecb896925', '魏五', 25, 1, '10023', '18600509026', '白鹿原', 0.00, 1, 0,'2021-07-03 14:46:23', NULL, NULL);",
                "INSERT INTO student VALUES ('002f2dcb10bafdfd4be0adcecb896925', '佟五', 25, 1, '10023', '18600509027', '白鹿原', 0.00, 1, 0,'2021-07-03 14:46:23', NULL, NULL);",
                "INSERT INTO student VALUES ('002f2dcb10ba4be0adasaacecb896925', '李五', 25, 1, '10023', '18600509028', '白鹿原', 0.00, 1, 0,'2021-07-03 14:46:23', NULL, NULL);",
                "INSERT INTO student VALUES ('002f2dcbdwre10ba4be0adcecb896925', '孙五', 25, 1, '10023', '18600509029', '白鹿原', 0.00, 1, 0,'2021-07-03 14:46:23', NULL, NULL);",
                "INSERT INTO student VALUES ('002f2dcb10ba4be0adfertcecb896925', '汤五', 25, 1, '10023', '18600509032', '白鹿原', 0.00, 1, 0,'2021-07-03 14:46:23', NULL, NULL);",
                "INSERT INTO student VALUES ('002f2dcb10ba4byytte0adcecb896925', '唐五', 25, 1, '10023', '18600509042', '白鹿原', 0.00, 1, 0,'2021-07-03 14:46:23', NULL, NULL);"
        };
    }

}