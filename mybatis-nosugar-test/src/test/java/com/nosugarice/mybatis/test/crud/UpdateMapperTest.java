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
import com.nosugarice.mybatis.criteria.tocolumn.LambdaUpdate;
import com.nosugarice.mybatis.test.BaseMapperTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/20
 */
class UpdateMapperTest extends BaseMapperTest {

    @Test
    void updateById() {
        StudentMapper mapper = getMapper(StudentMapper.class);
        Student student = mapper.selectById("002f2dcb10ba4be0adc333cecb886111").orElseThrow(NullPointerException::new);
        Assertions.assertEquals(12, student.getAge());

        student.setAge(20);
        mapper.updateById(student);
        student = mapper.selectById("002f2dcb10ba4be0adc333cecb886111").orElseThrow(NullPointerException::new);
        Assertions.assertEquals(20, student.getAge());
    }

    @Test
    void updateByIdChoseKey() {
        StudentMapper mapper = getMapper(StudentMapper.class);
        Student student = mapper.selectById("002f2dcb10ba4be0adc333cecb886111").orElseThrow(NullPointerException::new);
        Assertions.assertEquals(12, student.getAge());

        student.setAge(20);
        student.setAddress("长安");
        student.setName("李四");
        mapper.updateByIdChoseKey(student, Stream.of("address", "name").collect(Collectors.toSet()));

        Student studentAfter = mapper.selectById("002f2dcb10ba4be0adc333cecb886111").orElseThrow(NullPointerException::new);
        Assertions.assertAll(
                () -> Assertions.assertEquals(12, studentAfter.getAge()),
                () -> Assertions.assertEquals("长安", studentAfter.getAddress()),
                () -> Assertions.assertEquals("李四", studentAfter.getName())
        );
    }

    @Test
    void updateByIdNullable() {
        StudentMapper mapper = getMapper(StudentMapper.class);
        Student student = mapper.selectById("002f2dcb10ba4be0adc333cecb886111").orElseThrow(NullPointerException::new);
        Assertions.assertEquals(12, student.getAge());

        student.setAge(null);
        mapper.updateByIdNullable(student);

        student = mapper.selectById("002f2dcb10ba4be0adc333cecb886111").orElseThrow(NullPointerException::new);
        Assertions.assertEquals(12, student.getAge());
    }

    @Test
    void update() {
        StudentMapper mapper = getMapper(StudentMapper.class);

        LambdaUpdate<Student> criteriaUpdate = CriteriaBuilder.lambdaUpdate(Student.class);

        criteriaUpdate.set(Student::getAge, 30);
        criteriaUpdate.equalTo(Student::getPhone, "18600509022");
        mapper.update(criteriaUpdate);

        Student student = mapper.selectById("002f2dcb10ba4be0adc333cecb886111").orElseThrow(NullPointerException::new);
        Assertions.assertEquals(30, student.getAge());
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
                "INSERT INTO student VALUES ('002f2dcb10ba4be0adc333cecb886111', '张三', 12, 1, '10021', '18600509022', '南京', 0.00, 0, 0,'2021-07-03 14:46:23', NULL, NULL);"
        };
    }

    @Override
    public Class<?>[] withMapper() {
        return new Class<?>[]{
                StudentMapper.class
        };
    }
}
