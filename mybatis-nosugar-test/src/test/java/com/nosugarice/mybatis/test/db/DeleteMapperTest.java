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
