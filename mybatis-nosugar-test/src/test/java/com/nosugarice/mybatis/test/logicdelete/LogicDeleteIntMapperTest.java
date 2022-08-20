package com.nosugarice.mybatis.test.logicdelete;

import com.nosugarice.mybatis.criteria.CriteriaBuilder;
import com.nosugarice.mybatis.criteria.tocolumn.LambdaQuery;
import com.nosugarice.mybatis.test.BaseMapperTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/20
 */
class LogicDeleteIntMapperTest extends BaseMapperTest {

    public static final User USER = new User();

    static {
        USER.setId(110);
        USER.setName("小白");
    }

    @Test
    void logicDeleteById() {
        StudentMapper mapper = getMapper(StudentMapper.class);
        String userId = "002f2dcb10ba4be0adc333cecb886111";

        Student student = new Student();
        student.setId(userId);
        student.setName("小王吧");
        mapper.insert(student);

        mapper.selectById(userId).ifPresent(studentTemp -> {
            Assertions.assertEquals(userId, studentTemp.getId());
            Assertions.assertEquals(0, studentTemp.getDisabled());
        });

        LogicDeleteDisabledByHandler.setUserId(USER.getId());
        LogicDeleteDisabledNameHandler.setUserName(USER.getName());

        int delete = mapper.logicDeleteById(userId);
        Assertions.assertEquals(1, delete);

        LambdaQuery<Student> query = CriteriaBuilder.lambdaQuery(Student.class);
        query.equalTo(Student::getId, userId).includeLogicDelete();

        List<Student> students = mapper.selectList(query);
        Assertions.assertEquals(1, students.size());
        Student logicDeleteStudent = students.get(0);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, logicDeleteStudent.getDisabled()),
                () -> Assertions.assertEquals(110, logicDeleteStudent.getDisabledBy()),
                () -> Assertions.assertEquals("小白", logicDeleteStudent.getDisabledName())
        );

        boolean exists = mapper.selectById(userId).isPresent();
        Assertions.assertFalse(exists);
    }

    @Override
    public String[] withScriptPath() {
        return new String[]{
                "/db/logicdelete/student_schema.sql"
        };
    }


    @Override
    public Class<?>[] withMapper() {
        return new Class<?>[]{
                StudentMapper.class
        };
    }

}
