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

package com.nosugarice.mybatis.assign.value;

import com.nosugarice.mybatis.annotation.LogicDelete;
import com.nosugarice.mybatis.exception.NoSugarException;
import com.nosugarice.mybatis.handler.ValueHandler;
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
 * 逻辑删除的初始值和删除值是确定的,直接拼接到sql
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/12/6
 */
public class LogicDeleteValue extends SimpleValue {

    /** 默认值 */
    private final String defaultValue;

    /** 逻辑删除值 */
    private final String logicDeleteValue;

    public LogicDeleteValue(Class<?> type, String defaultValue, String logicDeleteValue) {
        super(type);
        this.defaultValue = defaultValue;
        this.logicDeleteValue = logicDeleteValue;
    }

    @Override
    public boolean isInsertable() {
        return true;
    }

    @Override
    public boolean isUpdateable() {
        return false;
    }

    @Override
    public boolean isLogicDelete() {
        return true;
    }

    @Override
    public ValueHandler<Serializable> insertHandler() {
        return value -> getDefaultValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ValueHandler<Serializable> logicDeleteHandler() {
        if (super.logicDeleteHandler() != null) {
            return (ValueHandler<Serializable>) super.logicDeleteHandler();
        }
        return value -> LogicDeleteValueGenerator.INSTANCE.generateValue(logicDeleteValue, getType());
    }

    @Override
    public ValueHandler<?> conditionHandler() {
        if (super.conditionHandler() != null) {
            return super.conditionHandler();
        }
        return (ValueHandler<Serializable>) value -> getDefaultValue();
    }

    @Override
    public Serializable getDefaultValue() {
        return LogicDeleteValueGenerator.INSTANCE.generateValue(defaultValue, getType());
    }

    private static class LogicDeleteValueGenerator {

        private static final Set<Class<?>> SUPPORTS_LOGIC_DELETE_TYPE = Stream.of(int.class, Integer.class, long.class
                        , Long.class, boolean.class, Boolean.class, Date.class, LocalDateTime.class, String.class)
                .collect(Collectors.toSet());

        private static final LogicDeleteValueGenerator INSTANCE = new LogicDeleteValueGenerator();

        public Serializable generateValue(String value, Class<?> type) {
            Preconditions.checkArgument(SUPPORTS_LOGIC_DELETE_TYPE.contains(type)
                    , "不支持的逻辑删除字段类型" + "[" + type.getName() + "]");

            if (LogicDelete.NULL.equalsIgnoreCase(value)) {
                return null;
            }

            if (LogicDelete.NOW.equalsIgnoreCase(value)) {
                value = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }

            if (String.class.isAssignableFrom(type)) {
                return value;
            }
            if (Integer.class.isAssignableFrom(type)) {
                return Integer.valueOf(value);
            } else if (Long.class.isAssignableFrom(type)) {
                return Long.valueOf(value);
            } else if (LocalDateTime.class.isAssignableFrom(type)) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return LocalDateTime.parse(value, dateTimeFormatter);
            } else if (Date.class.isAssignableFrom(type)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    return dateFormat.parse(value);
                } catch (ParseException e) {
                    throw new NoSugarException(e);
                }
            } else if (Boolean.class.isAssignableFrom(type)) {
                return Boolean.valueOf(value);
            }
            return null;
        }

    }

}
