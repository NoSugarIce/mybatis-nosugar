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

import com.nosugarice.mybatis.handler.ValueHandler;
import com.nosugarice.mybatis.util.Preconditions;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/6
 */
public class VersionValue extends SimpleValue {

    private static final Set<Class<?>> SUPPORTS_LOGIC_DELETE_TYPE = Stream.of(int.class, Integer.class
                    , long.class, Long.class, Date.class, Timestamp.class, LocalDateTime.class)
            .collect(Collectors.toSet());

    private static final Map<Class<?>, VersionValue> VERSION_VALUE_CACHE = new HashMap<>(SUPPORTS_LOGIC_DELETE_TYPE.size(), 1);

    private final VersionDefaultValueHandler defaultValueHandler;

    public static VersionValue of(Class<?> type) {
        return VERSION_VALUE_CACHE.computeIfAbsent(type, VersionValue::new);
    }

    private VersionValue(Class<?> type) {
        super(type);
        Preconditions.checkArgument(SUPPORTS_LOGIC_DELETE_TYPE.contains(type), "版本号字段不支持类型:" + type);
        this.defaultValueHandler = new VersionDefaultValueHandler(type);
    }

    @Override
    public ValueHandler<?> insertHandler() {
        return defaultValueHandler;
    }

    @Override
    public ValueHandler<?> updateHandler() {
        return VersionValueHandler.getInstance();
    }

    private static class VersionDefaultValueHandler implements ValueHandler<Serializable> {

        private final Class<?> type;

        public VersionDefaultValueHandler(Class<?> type) {
            this.type = type;
        }

        @Override
        public Serializable setValue(Serializable value) {
            switch (type.getName()) {
                case "int":
                case "java.lang.Integer":
                    return 0;
                case "long":
                case "java.lang.Long":
                    return 0L;
                case "java.sql.Timestamp":
                    return new Timestamp(System.currentTimeMillis());
                case "java.util.Date":
                    return new Date();
                case "java.time.LocalDateTime":
                    return LocalDateTime.now();
                default:
                    return null;
            }
        }
    }

    private static class VersionValueHandler implements ValueHandler<Serializable> {

        private static VersionValueHandler getInstance() {
            return Holder.INSTANCE;
        }

        private static class Holder {
            private static final VersionValueHandler INSTANCE = new VersionValueHandler();
        }

        @Override
        public Serializable setValue(Serializable value) {
            if (value == null) {
                return null;
            }
            Class<? extends Serializable> type = value.getClass();
            Preconditions.checkArgument(SUPPORTS_LOGIC_DELETE_TYPE.contains(type)
                    , "不支持的乐观锁版本字段类型" + "[" + type.getName() + "]");

            if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
                return (Integer) value + 1;
            } else if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
                return (Long) value + 1L;
            } else if (Date.class.isAssignableFrom(type)) {
                return new Date();
            } else if (Timestamp.class.isAssignableFrom(type)) {
                return new Timestamp(System.currentTimeMillis());
            } else if (LocalDateTime.class.isAssignableFrom(type)) {
                return LocalDateTime.now();
            }
            return null;
        }
    }

}
