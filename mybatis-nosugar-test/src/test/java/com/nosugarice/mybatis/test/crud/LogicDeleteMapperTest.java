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
import com.nosugarice.mybatis.criteria.tocolumn.LambdaDelete;
import com.nosugarice.mybatis.test.BaseMapperTest;
import com.nosugarice.mybatis.test.base.mode.StatusEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/20
 */
class LogicDeleteMapperTest extends BaseMapperTest {

    @Test
    void logicDeleteById() {
        StudentMapper mapper = getMapper(StudentMapper.class);
        int delete = mapper.logicDeleteById("002f2dcb10ba4be0adc333cecb886111");
        Assertions.assertEquals(1, delete);
        boolean exists = mapper.selectById("002f2dcb10ba4be0adc333cecb886111").isPresent();
        Assertions.assertFalse(exists);
    }

    @Test
    void logicDeleteByIds() {
        StudentMapper mapper = getMapper(StudentMapper.class);
        int delete = mapper.logicDeleteByIds(
                Arrays.asList("002f2dcb10ba4be0adc333cecb886111", "002f2dcb10ba4be0adc333cecb896118"));
        Assertions.assertEquals(2, delete);

        boolean exists = mapper.selectById("002f2dcb10ba4be0adc333cecb886111").isPresent();
        Assertions.assertFalse(exists);
    }

    @Test
    void defaultLogicDeleteByIds() {
        StudentMapper mapper = getMapper(StudentMapper.class);
        int delete = mapper.logicDeleteByIds("002f2dcb10ba4be0adc333cecb886111", "002f2dcb10ba4be0adc333cecb896118");
        Assertions.assertEquals(2, delete);

        boolean exists = mapper.selectById("002f2dcb10ba4be0adc333cecb886111").isPresent();
        Assertions.assertFalse(exists);
    }

    @Test
    void logicDelete() {
        StudentMapper mapper = getMapper(StudentMapper.class);

        LambdaDelete<Student> query = CriteriaBuilder.lambdaDelete(Student.class);
        query.equalTo(Student::getStatus, StatusEnum.ON)
                .between(Student::getAge, 10, 30)
                .lessThan(Student::getCardBalance, new BigDecimal("200"));

        int delete = mapper.logicDelete(query);
        Assertions.assertEquals(2, delete);
    }

    @Test
    void defaultLogicDelete() {
        StudentMapper mapper = getMapper(StudentMapper.class);

        Student student = new Student();
        student.setStatus(StatusEnum.OFF);

        int delete = mapper.logicDelete(student);
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
