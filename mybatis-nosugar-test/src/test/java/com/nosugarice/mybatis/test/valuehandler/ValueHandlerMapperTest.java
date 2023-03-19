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
        studentA.setName("王小二");
        mapper.updateById(studentA);

        Student studentB = mapper.selectById(student.getId()).orElseThrow(NullPointerException::new);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(studentB.getUpdatedAt()),
                () -> Assertions.assertTrue(LocalDateTime.now().isAfter(studentB.getUpdatedAt()))
        );

        Student studentC = new Student();
        studentC.setName("王小二");

        Student studentD = mapper.selectFirst(studentC);

        Assertions.assertAll(
                () -> Assertions.assertEquals("王小二-假装加密-假装解密", studentD.getName())
        );
    }

    @Override
    public String[] withScript() {
        return new String[]{
                "INSERT INTO student VALUES ('002f2dcb10ba4be0adc333cecb886111', '王小二', 18, 0, 1, '18600509022', '南京', 0.00, 0, 0,'2021-07-03 14:46:23', NULL, NULL);",
        };
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
