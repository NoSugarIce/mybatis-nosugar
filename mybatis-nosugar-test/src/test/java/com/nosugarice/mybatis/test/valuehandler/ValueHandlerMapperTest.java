package com.nosugarice.mybatis.test.valuehandler;

import com.nosugarice.mybatis.test.BaseMapperTest;
import com.nosugarice.mybatis.test.base.mode.StatusEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/20
 */
class ValueHandlerMapperTest extends BaseMapperTest {
    @Test
    public void valueHandler() {
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

        Student studentA = mapper.selectById(student.getId()).orElseThrow(NullPointerException::new);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(studentA.getCreatedAt()),
                () -> Assertions.assertTrue(LocalDateTime.now().isAfter(studentA.getCreatedAt())),
                () -> Assertions.assertNull(studentA.getUpdatedAt()),
                () -> Assertions.assertEquals("王小二-假装加密-假装解密", studentA.getName())
        );

        studentA.setCardBalance(new BigDecimal("100"));
        mapper.updateById(studentA);

        Student studentB = mapper.selectById(studentA.getId()).orElseThrow(NullPointerException::new);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(studentB.getUpdatedAt()),
                () -> Assertions.assertTrue(LocalDateTime.now().isAfter(studentB.getUpdatedAt()))
        );

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