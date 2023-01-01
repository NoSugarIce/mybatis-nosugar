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

package com.nosugarice.mybatis.test.logicdelete.deleteValueHandler;

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
class LogicDeleteValueHandlerMapperTest extends BaseMapperTest {

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

        int delete = mapper.logicDeleteById(userId);
        Assertions.assertEquals(1, delete);

        LambdaQuery<Student> query = CriteriaBuilder.lambdaQuery(Student.class);
        query.equalTo(Student::getId, userId).includeLogicDelete();

        List<Student> students = mapper.selectList(query);
        Assertions.assertEquals(1, students.size());
        Student logicDeleteStudent = students.get(0);

        Assertions.assertTrue(logicDeleteStudent.getDisabled() > 1);

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
