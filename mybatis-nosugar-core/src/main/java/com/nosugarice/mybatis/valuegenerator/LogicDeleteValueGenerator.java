/*
 *    Copyright 2021 NoSugarIce
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nosugarice.mybatis.valuegenerator;

import com.nosugarice.mybatis.exception.NoSugarException;
import com.nosugarice.mybatis.util.Preconditions;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/6
 */
public class LogicDeleteValueGenerator implements ValueGenerator<Serializable, AnnotationParameter> {

    public static final Set<Class<?>> SUPPORTS_LOGIC_DELETE_TYPE = Stream.of(int.class, Integer.class, long.class
            , Long.class, boolean.class, Boolean.class, Date.class, LocalDateTime.class, String.class)
            .collect(Collectors.toSet());

    private LogicDeleteValueGenerator() {
    }

    public static LogicDeleteValueGenerator getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public Serializable generateValue(AnnotationParameter parameter) {
        Preconditions.checkArgument(SUPPORTS_LOGIC_DELETE_TYPE.contains(parameter.getFiledType()), true
                , "不支持的逻辑删除字段类型" + "[" + parameter.getFiledType().getName() + "]");

        String value = parameter.getValue();
        if ("NULL".equalsIgnoreCase(value)) {
            return null;
        }
        if (String.class.isAssignableFrom(parameter.getFiledType())) {
            return value;
        }
        if (Integer.class.isAssignableFrom(parameter.getFiledType())) {
            return Integer.valueOf(value);
        } else if (Long.class.isAssignableFrom(parameter.getFiledType())) {
            return Long.valueOf(value);
        } else if (LocalDateTime.class.isAssignableFrom(parameter.getFiledType())) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(value, dateTimeFormatter);
        } else if (Date.class.isAssignableFrom(parameter.getFiledType())) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return dateFormat.parse(value);
            } catch (ParseException e) {
                throw new NoSugarException(e);
            }
        } else if (Boolean.class.isAssignableFrom(parameter.getFiledType())) {
            return Boolean.valueOf(value);
        }
        return null;
    }

    private static class Holder {
        private static final LogicDeleteValueGenerator INSTANCE = new LogicDeleteValueGenerator();
    }

}
