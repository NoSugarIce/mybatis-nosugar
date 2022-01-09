package com.nosugarice.mybatis.test.jpa;

import com.nosugarice.mybatis.test.BaseMapperTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/20
 */
class JpaMapperTest extends BaseMapperTest {

    @Test
    void findByNameStartsWithAndAgeBetween() {
        StudentMapper mapper = getMapper(StudentMapper.class);
        List<Student> students = mapper.findByNameStartsWithAndAgeBetween("张", 10, 13);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, students.size()),
                () -> Assertions.assertEquals("张三", students.get(0).getName())
        );
    }

    @Test
    void findByNameStartsWithOrAgeBetween() {
        StudentMapper mapper = getMapper(StudentMapper.class);
        List<Student> students = mapper.findByNameStartsWithOrAgeBetween("张", 18, 22);

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, students.size()),
                () -> Assertions.assertEquals("张三", students.get(0).getName()),
                () -> Assertions.assertEquals("李四", students.get(1).getName())
        );
    }

    @Test
    void countByNameStartsWithAndAgeBetween() {
        StudentMapper mapper = getMapper(StudentMapper.class);
        long count = mapper.countByNameStartsWithAndAgeBetween("张", 10, 13);

        Assertions.assertEquals(1, count);
    }

    @Test
    void existsByNameStartsWithAndAgeBetween() {
        StudentMapper mapper = getMapper(StudentMapper.class);
        Optional<Integer> exists = mapper.existsByNameStartsWithAndAgeBetween("张", 10, 13);

        Assertions.assertTrue(exists.isPresent());
    }

    @Test
    void deleteByNameStartsWithAndAgeBetween() {
        StudentMapper mapper = getMapper(StudentMapper.class);
        int delete = mapper.deleteByNameStartsWithAndAgeBetween("张", 10, 13);

        Assertions.assertEquals(1, delete);
    }

    @Test
    void logicDeleteByNameStartsWithAndAgeBetween() {
        StudentMapper mapper = getMapper(StudentMapper.class);
        int delete = mapper.logicDeleteByNameStartsWithAndAgeBetween("张", 10, 13);

        Assertions.assertEquals(1, delete);
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
                "INSERT INTO student VALUES ('002f2dcb10ba4be0adc333cecb886111', '张三', 12, 1, '10021', '18600509022', '南京', 0.00, 0, 0,'2021-07-03 14:46:23', NULL, NULL);",
                "INSERT INTO student VALUES ('002f2dcb10ba4be0adc333cecb896118', '李四', 20, 1, '10022', '18600509025', '驻马店', 0.00, 0, 0,'2021-07-03 14:46:23', NULL, NULL);",
                "INSERT INTO student VALUES ('002f2dcb10ba4be0adc333cecb896925', '王五', 25, 1, '10023', '18600509026', '白鹿原', 0.00, 0, 0,'2021-07-03 14:46:23', NULL, NULL);"
        };
    }

    @Override
    public Class<?>[] withMapper() {
        return new Class<?>[]{
                StudentMapper.class
        };
    }
}