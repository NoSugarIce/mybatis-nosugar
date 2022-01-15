package com.nosugarice.mybatis.test.parentproperty;

import com.nosugarice.mybatis.test.BaseMapperTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/20
 */
class MapperTest extends BaseMapperTest {

    @Test
    void selectById() {
        StudentMapper mapper = getMapper(StudentMapper.class);
        Student student = mapper.selectById("002f2dcb10ba4be0adc333cecb886111").orElse(null);
        Assertions.assertNull(student);
    }

    @Override
    public String[] withScriptPath() {
        return new String[]{
                "/db/student_schema.sql"
        };
    }

    @Override
    public String[] withScript() {
        return new String[]{
        };
    }

    @Override
    public Class<?>[] withMapper() {
        return new Class<?>[]{
                StudentMapper.class
        };
    }
}