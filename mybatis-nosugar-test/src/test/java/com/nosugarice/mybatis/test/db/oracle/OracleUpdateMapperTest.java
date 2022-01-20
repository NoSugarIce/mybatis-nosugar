package com.nosugarice.mybatis.test.db.oracle;

import com.nosugarice.mybatis.test.db.UpdateMapperTest;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/20
 */
class OracleUpdateMapperTest extends UpdateMapperTest implements OracleTest {

    @Override
    public String[] withScript() {
        return new String[]{
                "INSERT INTO STUDENT VALUES ('002f2dcb10ba4be0adc333cecb886111', '张三', 12, 1, '10021', '18600509022', '南京', 0.00, 0, 0,TO_DATE('2021-07-03 14:46:23', 'SYYYY-MM-DD HH24:MI:SS' ), NULL, NULL);"
        };
    }

}