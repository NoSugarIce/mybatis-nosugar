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

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/6
 */
public class VersionValue extends SimpleValue {

    private static final Map<Class<?>, VersionValue> VERSION_VALUE_CACHE = new ConcurrentHashMap<>(VersionType.values().length);

    private final VersionType versionType;

    public static VersionValue of(Class<?> type) {
        return VERSION_VALUE_CACHE.computeIfAbsent(type, VersionValue::new);
    }

    private VersionValue(Class<?> type) {
        super(type);
        this.versionType = VersionType.of(type);
    }

    @Override
    public ValueHandler<?> updateHandler() {
        return (ValueHandler<Serializable>) versionType::nextValue;
    }

    @Override
    public ValueHandler<?> fillHandler() {
        return value -> versionType.getDefaultValue();
    }

    @Override
    public boolean isInsertFill() {
        return true;
    }

    private enum VersionType {
        INT(Integer.class, int.class, () -> 0, (Serializable value) -> (Integer) value + 1),
        LONG(Long.class, long.class, () -> 0L, (Serializable value) -> (Long) value + 1L),
        DATE(Date.class, Date.class, Date::new, s -> new Date()),
        TIMESTAMP(Timestamp.class, Timestamp.class, () -> new Timestamp(System.currentTimeMillis())
                , s -> new Timestamp(System.currentTimeMillis())),
        LOCAL_DATETIME(LocalDateTime.class, LocalDateTime.class, LocalDateTime::now, s -> LocalDateTime.now());

        private final Class<?> type;
        private final Class<?> primitiveType;
        private final Supplier<Serializable> defaultValueSupplier;
        private final Function<Serializable, Serializable> nextValueFunction;

        private VersionType(Class<?> type, Class<?> primitiveType, Supplier<Serializable> defaultValueSupplier
                , Function<Serializable, Serializable> nextValueFunction) {
            this.type = type;
            this.primitiveType = primitiveType;
            this.defaultValueSupplier = defaultValueSupplier;
            this.nextValueFunction = nextValueFunction;
        }

        public Class<?> getType() {
            return type;
        }

        public Class<?> getPrimitiveType() {
            return primitiveType;
        }

        public Serializable getDefaultValue() {
            return defaultValueSupplier.get();
        }

        public Serializable nextValue(Serializable value) {
            return nextValueFunction.apply(value);
        }

        public static VersionType of(Class<?> type) {
            return Stream.of(values())
                    .filter(versionType -> versionType.getType().isAssignableFrom(type)
                            || versionType.getPrimitiveType().isAssignableFrom(type))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("版本号字段不支持类型:" + type));
        }

    }

}
