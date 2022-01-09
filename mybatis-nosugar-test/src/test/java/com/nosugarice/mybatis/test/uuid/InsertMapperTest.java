package com.nosugarice.mybatis.test.uuid;

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
        StudentMapper studentMapper = getMapper(StudentMapper.class);
        Student student = new Student();
        student.setName("王小二");

        studentMapper.insert(student);
        Assertions.assertNotNull(student.getId());
        Assertions.assertTrue(student.getId().length() > 0);
    }

    @Override
    public String[] withScriptPath() {
        return new String[]{
                "/db/insert/student_schema_uuid.sql"
        };
    }

    @Override
    public Class<?>[] withMapper() {
        return new Class<?>[]{
                StudentMapper.class
        };
    }

}