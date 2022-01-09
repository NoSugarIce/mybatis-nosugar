package com.nosugarice.mybatis.test.crud;

import com.nosugarice.mybatis.test.BaseMapperTest;
import com.nosugarice.mybatis.test.base.mode.StatusEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

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
        student.setAge(18);
        student.setSex(0);
        student.setSno(1);
        student.setPhone("186");
        student.setAddress("南京");
        student.setCardBalance(new BigDecimal("0"));
        student.setStatus(StatusEnum.ON);

        mapper.insert(student);

        Assertions.assertNotNull(student.getId());
    }

    @Test
    public void insertNullable() {
        StudentMapper mapper = getMapper(StudentMapper.class);
        Student student = new Student();
        student.setName("王小二");
        student.setAge(18);
        student.setSex(0);
        student.setSno(1);
        student.setStatus(StatusEnum.ON);

        mapper.insertNullable(student);

        Assertions.assertNotNull(student.getId());
    }

    @Override
    public String[] withScriptPath() {
        return new String[]{
                "/db/student_schema.sql"
        };
    }

    @Override
    public Class<?>[] withMapper() {
        return new Class<?>[]{
                StudentMapper.class
        };
    }

}