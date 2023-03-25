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

package com.nosugarice.mybatis.test.identity;

import com.nosugarice.mybatis.test.BaseMapperTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/17
 */
class InsertPrimaryKeyStrategyMapperTest extends BaseMapperTest {

    @Test
    public void insert() {
        StudentMapper mapper = getMapper(StudentMapper.class);
        Student student = new Student();
        student.setName("王小二");

        mapper.insert(student);
        Assertions.assertEquals(student.getId(), 0);

        student.setId(null);
        mapper.insert(student);
        Assertions.assertEquals(student.getId(), 1);
    }

    @Override
    public String[] withScriptPath() {
        return new String[]{
                "/db/insert/student_schema_identity.sql"
        };
    }

    @Override
    public Class<?>[] withMapper() {
        return new Class<?>[]{
                StudentMapper.class
        };
    }

}
