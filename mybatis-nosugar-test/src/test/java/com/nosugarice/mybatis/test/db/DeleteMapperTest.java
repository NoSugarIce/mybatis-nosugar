package com.nosugarice.mybatis.test.db;

import com.nosugarice.mybatis.criteria.CriteriaBuilder;
import com.nosugarice.mybatis.criteria.tocolumn.LambdaDelete;
import com.nosugarice.mybatis.test.base.mode.StatusEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/20
 */
public abstract class DeleteMapperTest extends BaseDbMapperTest {

    @Test
    void deleteById() {
        StudentMapper mapper = getMapper();
        int delete = mapper.deleteById("002f2dcb10ba4be0adc333cecb886111");
        Assertions.assertEquals(1, delete);
        boolean exists = mapper.selectById("002f2dcb10ba4be0adc333cecb886111").isPresent();
        Assertions.assertFalse(exists);
    }

    @Test
    void deleteByIds() {
        StudentMapper mapper = getMapper();
        int delete = mapper.deleteByIds(
                Arrays.asList("002f2dcb10ba4be0adc333cecb886111", "002f2dcb10ba4be0adc333cecb896118"));
        Assertions.assertEquals(2, delete);

        boolean exists = mapper.selectById("002f2dcb10ba4be0adc333cecb886111").isPresent();
        Assertions.assertFalse(exists);
    }

    @Test
    void defaultDeleteByIds() {
        StudentMapper mapper = getMapper();
        int delete = mapper.deleteByIds("002f2dcb10ba4be0adc333cecb886111", "002f2dcb10ba4be0adc333cecb896118");
        Assertions.assertEquals(2, delete);

        boolean exists = mapper.selectById("002f2dcb10ba4be0adc333cecb886111").isPresent();
        Assertions.assertFalse(exists);
    }

    @Test
    void delete() {
        StudentMapper mapper = getMapper();

        LambdaDelete<Student> query = CriteriaBuilder.lambdaDelete(getEntityClass());
        query.equalTo(Student::getStatus, StatusEnum.ON)
                .between(Student::getAge, 10, 30)
                .lessThan(Student::getCardBalance, new BigDecimal("200"));

        int delete = mapper.delete(query);
        Assertions.assertEquals(2, delete);
    }

    @Test
    void deleteByEntity() {
        StudentMapper mapper = getMapper();

        Student student = crateEntity();
        student.setStatus(StatusEnum.OFF);

        int delete = mapper.delete(student);
        Assertions.assertEquals(1, delete);
    }

}