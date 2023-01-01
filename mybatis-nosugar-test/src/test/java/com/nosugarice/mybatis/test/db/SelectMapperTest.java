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

package com.nosugarice.mybatis.test.db;

import com.nosugarice.mybatis.criteria.CriteriaBuilder;
import com.nosugarice.mybatis.criteria.tocolumn.LambdaQuery;
import com.nosugarice.mybatis.domain.Page;
import com.nosugarice.mybatis.domain.PageImpl;
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
public abstract class SelectMapperTest extends BaseDbMapperTest {

    @Test
    public void selectById() {
        StudentMapper mapper = getMapper();
        Student student = mapper.selectById("002f2dcb10ba4be0adc333cecb886111").orElseThrow(NullPointerException::new);
        Assertions.assertEquals(12, student.getAge());
    }

    @Test
    void selectByIds() {
        StudentMapper mapper = getMapper();
        List<Student> students = mapper.selectByIds(
                Arrays.asList("002f2dcb10ba4be0adc333cecb886111", "002f2dcb10ba4be0adc333cecb896118"));
        Assertions.assertEquals(2, students.size());
    }

    @Test
    void defaultSelectByIds() {
        StudentMapper mapper = getMapper();
        List<Student> students = mapper.selectByIds("002f2dcb10ba4be0adc333cecb886111", "002f2dcb10ba4be0adc333cecb896118");
        Assertions.assertEquals(2, students.size());
    }

    @Test
    void selectList() {
        StudentMapper mapper = getMapper();

        LambdaQuery<Student> query = CriteriaBuilder.lambdaQuery(getEntityClass());
        query.equalTo(Student::getStatus, StatusEnum.ON)
                .between(Student::getAge, 10, 30)
                .lessThan(Student::getCardBalance, new BigDecimal("200"));

        List<Student> students = mapper.selectList(query);
        Assertions.assertEquals(2, students.size());
    }

    @Test
    void count() {
        StudentMapper mapper = getMapper();

        LambdaQuery<Student> query = CriteriaBuilder.lambdaQuery(getEntityClass());
        query.equalTo(Student::getStatus, StatusEnum.ON)
                .between(Student::getAge, 10, 30)
                .lessThan(Student::getCardBalance, new BigDecimal("200"));

        long count = mapper.count(query);
        Assertions.assertEquals(2, count);
    }

    @Test
    void exists() {
        StudentMapper mapper = getMapper();

        LambdaQuery<Student> query = CriteriaBuilder.lambdaQuery(getEntityClass());
        query.equalTo(Student::getStatus, StatusEnum.ON)
                .between(Student::getAge, 10, 30)
                .lessThan(Student::getCardBalance, new BigDecimal("200"));

        Optional<Integer> exists = mapper.exists(query);
        Assertions.assertTrue(exists.isPresent());
    }

    @Test
    void defaultSelectAll() {
        StudentMapper mapper = getMapper();
        List<Student> students = mapper.selectAll();
        Assertions.assertEquals(13, students.size());
    }


    @Test
    void defaultCount() {
        StudentMapper mapper = getMapper();

        Student student = crateEntity();
        student.setName("张三");
        student.setStatus(StatusEnum.ON);

        long count = mapper.count(student);
        Assertions.assertEquals(1, count);
    }

    @Test
    void defaultExists() {
        StudentMapper mapper = getMapper();

        Student student = crateEntity();
        student.setName("张三");
        student.setStatus(StatusEnum.OFF);

        boolean exists = mapper.exists(student);
        Assertions.assertFalse(exists);
    }

    @Test
    void defaultSelectOne() {
        StudentMapper mapper = getMapper();

        Student student = crateEntity();
        student.setName("张三");
        student.setStatus(StatusEnum.ON);

        Student studentA = mapper.selectOne(student);
        Assertions.assertNotNull(studentA);
    }

    @Test
    void defaultSelectList() {
        StudentMapper mapper = getMapper();

        Student student = crateEntity();
        student.setStatus(StatusEnum.OFF);

        List<Student> students = mapper.selectList(student);
        Assertions.assertEquals(11, students.size());
    }

    @Test
    void defaultSelectListLimit() {
        StudentMapper mapper = getMapper();

        Student student = crateEntity();
        student.setSex(1);

        List<Student> students = mapper.selectList(student, 2);
        Assertions.assertEquals(2, students.size());
    }

    @Test
    void defaultSelectListLimitByPage() {
        StudentMapper mapper = getMapper();

        Student student = crateEntity();
        student.setSex(1);

        List<Student> students = mapper.selectList(student, 2);
        Assertions.assertEquals(2, students.size());
    }

    @Test
    void defaultSelectListLimitByQueryPage() {
        StudentMapper mapper = getMapper();

        LambdaQuery<Student> query = CriteriaBuilder.lambdaQuery(getEntityClass());
        query.between(Student::getAge, 10, 30)
                .lessThan(Student::getCardBalance, new BigDecimal("200"));

        List<Student> students = mapper.selectList(query.limit(new PageImpl<>(2, 3)));
        Assertions.assertEquals(3, students.size());
    }

    @Test
    void defaultSelectPage() {
        StudentMapper mapper = getMapper();

        Student student = crateEntity();
        student.setSex(1);

        Page<Student> page = mapper.selectPage(student, new PageImpl<>(2, 3));
        Assertions.assertEquals(3, page.getNumberOfElements());
        Assertions.assertEquals(13, page.getTotal());
    }

    @Test
    void defaultSelectPageByQuery() {
        StudentMapper mapper = getMapper();

        LambdaQuery<Student> query = CriteriaBuilder.lambdaQuery(getEntityClass());
        query.between(Student::getAge, 10, 30)
                .lessThan(Student::getCardBalance, new BigDecimal("200"));

        Page<Student> page = mapper.selectPage(query, new PageImpl<>(2, 3));
        Assertions.assertEquals(3, page.getNumberOfElements());
        Assertions.assertEquals(13, page.getTotal());
    }

}
