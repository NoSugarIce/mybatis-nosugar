package com.nosugarice.mybatis.test.identity;

import com.nosugarice.mybatis.test.BaseMapperTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/17
 */
class InsertMapperTest extends BaseMapperTest {

    @Test
    public void insert() {
        StudentMapper mapper = getMapper(StudentMapper.class);
        Student student = new Student();
        student.setName("王小二");

        mapper.insert(student);
        Assertions.assertEquals(student.getId(), 0);

        student.setId(null);
        mapper.insert(student);
        Assertions.assertEquals(student.getId(), 1);
    }

    @Override
    public String[] withScriptPath() {
        return new String[]{
                "/db/insert/student_schema_identity.sql"
        };
    }

    @Override
    public Class<?>[] withMapper() {
        return new Class<?>[]{
                StudentMapper.class
        };
    }

}