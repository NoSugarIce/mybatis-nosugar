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

package com.nosugarice.mybatis.test.generichandler;

import com.nosugarice.mybatis.test.BaseMapperTest;
import com.nosugarice.mybatis.test.base.mode.StatusEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/20
 */
class JsonHandlerMapperTest extends BaseMapperTest {

    @Test
    public void genericHandler() {
        StudentMapper mapper = getMapper(StudentMapper.class);

        List<Book> books = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Book book = new Book();
            book.setName("book-" + i);
            books.add(book);
        }

        Student student = new Student();
        student.setName("王小二");
        student.setAge(18);
        student.setSex(0);
        student.setSno(1);
        student.setPhone("186");
        student.setAddress("南京");
        student.setCardBalance(new BigDecimal("0"));
        student.setStatus(StatusEnum.ON);
        student.setBooks(books);

        mapper.insert(student);

        Student studentA = mapper.selectById(student.getId()).orElseThrow(NullPointerException::new);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(studentA.getBooks()),
                () -> Assertions.assertEquals(studentA.getBooks().size(), 3)
        );

    }

    @Override
    public String[] withScript() {
        return new String[]{
        };
    }


    @Override
    public String[] withScriptPath() {
        return new String[]{
                "/db/generichandler/student_schema.sql"
        };
    }

    @Override
    public Class<?>[] withMapper() {
        return new Class<?>[]{
                StudentMapper.class
        };
    }
}
