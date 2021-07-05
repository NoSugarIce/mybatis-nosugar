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

import com.nosugarice.mybatis.util.Preconditions;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/6
 */
public class VersionValueGenerator implements ValueGenerator<Serializable, PropertyParameter> {

    public static final Set<Class<?>> SUPPORTS_LOGIC_DELETE_TYPE = Stream.of(int.class, Integer.class
            , long.class, Long.class, Date.class, Timestamp.class, LocalDateTime.class)
            .collect(Collectors.toSet());

    private VersionValueGenerator() {
    }

    public static VersionValueGenerator getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public Serializable generateValue(PropertyParameter parameter) {
        Preconditions.checkArgument(SUPPORTS_LOGIC_DELETE_TYPE.contains(parameter.getType()), true
                , "不支持的乐观锁版本字段类型" + "[" + parameter.getType().getName() + "]");

        if (int.class.isAssignableFrom(parameter.getType())
                || Integer.class.isAssignableFrom(parameter.getType())) {
            return ((Integer) parameter.getValue() + 1);
        } else if (long.class.isAssignableFrom(parameter.getType())
                || Long.class.isAssignableFrom(parameter.getType())) {
            return ((Long) parameter.getValue() + 1L);
        } else if (Date.class.isAssignableFrom(parameter.getType())) {
            return new Date();
        } else if (Timestamp.class.isAssignableFrom(parameter.getType())) {
            return new Timestamp(System.currentTimeMillis());
        } else if (LocalDateTime.class.isAssignableFrom(parameter.getType())) {
            return LocalDateTime.now();
        }
        return null;
    }

    private static class Holder {
        private static final VersionValueGenerator INSTANCE = new VersionValueGenerator();
    }

}
