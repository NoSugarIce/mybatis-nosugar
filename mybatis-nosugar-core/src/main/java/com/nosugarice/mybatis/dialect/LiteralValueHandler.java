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

package com.nosugarice.mybatis.dialect;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.nosugarice.mybatis.sql.SQLConstants.NULL;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/6/27
 */
public class LiteralValueHandler {

    private final Map<Class<? extends Serializable>, Function<Serializable, String>> handlerRegistry = new HashMap<>();

    protected LiteralValueHandler() {
        register(String.class, this::stringConvert);
        register(LocalDateTime.class, this::localDateTimeConvert);
        register(Date.class, this::dateConvert);
        register(Boolean.class, this::booleanConvert);
    }

    public void register(Class<? extends Serializable> clazz, Function<Serializable, String> function) {
        handlerRegistry.put(clazz, function);
    }

    /**
     * 转换为字面值
     *
     * @param value
     * @return
     */
    public String convert(Serializable value) {
        if (value == null) {
            return NULL;
        }
        Function<Serializable, String> function = handlerRegistry.getOrDefault(value.getClass(), this::originalConvert);
        return function.apply(value);
    }

    private String stringConvert(Serializable value) {
        return "'" + value + "'";
    }

    private String originalConvert(Serializable value) {
        return String.valueOf(value);
    }

    private String localDateTimeConvert(Serializable value) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return stringConvert(dateTimeFormatter.format((LocalDateTime) value));
    }

    private String dateConvert(Serializable value) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return stringConvert(dateFormat.format((Date) value));
    }

    private String booleanConvert(Serializable value) {
        return (Boolean) value ? "1" : "0";
    }

    public static LiteralValueHandler getLiteralValueHandler() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final LiteralValueHandler INSTANCE = new LiteralValueHandler();
    }

}
