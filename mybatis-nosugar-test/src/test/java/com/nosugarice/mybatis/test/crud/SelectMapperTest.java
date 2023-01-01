/*
 * Copyright 2021-2023 NoSugarIce
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nosugarice.mybatis.test.crud;

import com.nosugarice.mybatis.criteria.CriteriaBuilder;
import com.nosugarice.mybatis.criteria.tocolumn.LambdaQuery;
import com.nosugarice.mybatis.domain.Page;
import com.nosugarice.mybatis.domain.PageImpl;
import com.nosugarice.mybatis.test.BaseMapperTest;
import com.nosugarice.mybatis.test.base.mode.StatusEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/20
 */
class SelectMapperTest extends BaseMapperTest {

    @Test
    void selectById() {
        StudentMapper mapper = getMapper(StudentMapper.class);
        Student student = mapper.selectById("002f2dcb10ba4be0adc333cecb886111").orElseThrow(NullPointerException::new);
        Assertions.assertEquals(12, student.getAge());
    }

    @Test
    void selectByIds() {
        StudentMapper mapper = getMapper(StudentMapper.class);
        List<Student> students = mapper.selectByIds(
                Arrays.asList("002f2dcb10ba4be0adc333cecb886111", "002f2dcb10ba4be0adc333cecb896118"));
        Assertions.assertEquals(2, students.size());
    }

    @Test
    void defaultSelectByIds() {
        StudentMapper mapper = getMapper(StudentMapper.class);
        List<Student> students = mapper.selectByIds("002f2dcb10ba4be0adc333cecb886111", "002f2dcb10ba4be0adc333cecb896118");
        Assertions.assertEquals(2, students.size());
    }

    @Test
    void selectList() {
        StudentMapper mapper = getMapper(StudentMapper.class);

        LambdaQuery<Student> query = CriteriaBuilder.lambdaQuery(Student.class);
        query.equalTo(Student::getStatus, StatusEnum.ON)
                .between(Student::getAge, 10, 30)
                .lessThan(Student::getCardBalance, new BigDecimal("200"));

        List<Student> students = mapper.selectList(query);
        Assertions.assertEquals(2, students.size());
    }

    @Test
    void count() {
        StudentMapper mapper = getMapper(StudentMapper.class);

        LambdaQuery<Student> query = CriteriaBuilder.lambdaQuery(Student.class);
        query.equalTo(Student::getStatus, StatusEnum.ON)
                .between(Student::getAge, 10, 30)
                .lessThan(Student::getCardBalance, new BigDecimal("200"));

        long count = mapper.count(query);
        Assertions.assertEquals(2, count);
    }

    @Test
    void exists() {
        StudentMapper mapper = getMapper(StudentMapper.class);

        LambdaQuery<Student> query = CriteriaBuilder.lambdaQuery(Student.class);
        query.equalTo(Student::getStatus, StatusEnum.ON)
                .between(Student::getAge, 10, 30)
                .lessThan(Student::getCardBalance, new BigDecimal("200"));

        Optional<Integer> exists = mapper.exists(query);
        Assertions.assertTrue(exists.isPresent());
    }

    @Test
    void defaultSelectAll() {
        StudentMapper mapper = getMapper(StudentMapper.class);
        List<Student> students = mapper.selectAll();
        Assertions.assertEquals(3, students.size());
    }


    @Test
    void defaultCount() {
        StudentMapper mapper = getMapper(StudentMapper.class);

        Student student = new Student();
        student.setName("张三");
        student.setStatus(StatusEnum.ON);

        long count = mapper.count(student);
        Assertions.assertEquals(1, count);
    }

    @Test
    void defaultExists() {
        StudentMapper mapper = getMapper(StudentMapper.class);

        Student student = new Student();
        student.setName("张三");
        student.setStatus(StatusEnum.OFF);

        boolean exists = mapper.exists(student);
        Assertions.assertFalse(exists);
    }

    @Test
    void defaultSelectOne() {
        StudentMapper mapper = getMapper(StudentMapper.class);

        Student student = new Student();
        student.setName("张三");
        student.setStatus(StatusEnum.ON);

        Student studentA = mapper.selectOne(student);
        Assertions.assertNotNull(studentA);
    }

    @Test
    void defaultSelectList() {
        StudentMapper mapper = getMapper(StudentMapper.class);

        Student student = new Student();
        student.setStatus(StatusEnum.OFF);

        List<Student> students = mapper.selectList(student);
        Assertions.assertEquals(1, students.size());
    }

    @Test
    void defaultSelectListLimit() {
        StudentMapper mapper = getMapper(StudentMapper.class);

        Student student = new Student();
        student.setSex(1);

        List<Student> students = mapper.selectList(student, 2);
        Assertions.assertEquals(2, students.size());
    }

    @Test
    void defaultSelectListLimitByPage() {
        StudentMapper mapper = getMapper(StudentMapper.class);

        Student student = new Student();
        student.setSex(1);

        List<Student> students = mapper.selectList(student, 2);
        Assertions.assertEquals(2, students.size());
    }

    @Test
    void defaultSelectListLimitByQueryPage() {
        StudentMapper mapper = getMapper(StudentMapper.class);

        LambdaQuery<Student> query = CriteriaBuilder.lambdaQuery(Student.class);
        query.between(Student::getAge, 10, 30)
                .lessThan(Student::getCardBalance, new BigDecimal("200"));

        List<Student> students = mapper.selectList(query.limit(new PageImpl<>(2)));
        Assertions.assertEquals(2, students.size());
    }

    @Test
    void defaultSelectPage() {
        StudentMapper mapper = getMapper(StudentMapper.class);

        Student student = new Student();
        student.setSex(1);

        Page<Student> page = mapper.selectPage(student, new PageImpl<>(2));
        Assertions.assertEquals(2, page.getNumberOfElements());
        Assertions.assertEquals(3, page.getTotal());
    }

    @Test
    void defaultSelectPageByQuery() {
        StudentMapper mapper = getMapper(StudentMapper.class);

        LambdaQuery<Student> query = CriteriaBuilder.lambdaQuery(Student.class);
        query.between(Student::getAge, 10, 30)
                .lessThan(Student::getCardBalance, new BigDecimal("200"));

        Page<Student> page = mapper.selectPage(query, new PageImpl<>(2));
        Assertions.assertEquals(2, page.getNumberOfElements());
        Assertions.assertEquals(3, page.getTotal());
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
                "INSERT INTO student VALUES ('002f2dcb10ba4be0adc333cecb896118', '李四', 20, 1, '10022', '18600509025', '驻马店', 100.00, 0, 0,'2021-07-03 14:46:23', NULL, NULL);",
                "INSERT INTO student VALUES ('002f2dcb10ba4be0adc333cecb896925', '王五', 25, 1, '10023', '18600509026', '白鹿原', 0.00, 1, 0,'2021-07-03 14:46:23', NULL, NULL);"
        };
    }

    @Override
    public Class<?>[] withMapper() {
        return new Class<?>[]{
                StudentMapper.class
        };
    }
}
