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

import com.nosugarice.mybatis.test.base.mode.StatusEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/17
 */
public abstract class InsertMapperTest extends BaseDbMapperTest {

    @Test
    public void insert() {
        StudentMapper mapper = getMapper();
        Student student = crateEntity();
        student.setName("王小二");
        student.setAge(18);
        student.setSex(0);
        student.setSno(1);
        student.setPhone("186");
        student.setAddress("南京");
        student.setCardBalance(new BigDecimal("0"));
        student.setStatus(StatusEnum.ON);

        mapper.insert(student);

        Assertions.assertNotNull(student.getId());
    }

    @Test
    public void insertNullable() {
        StudentMapper mapper = getMapper();
        Student student = crateEntity();
        student.setName("王小二");
        student.setAge(18);
        student.setSex(0);
        student.setSno(1);
        student.setStatus(StatusEnum.ON);

        mapper.insertNullable(student);

        Assertions.assertNotNull(student.getId());
    }


}
