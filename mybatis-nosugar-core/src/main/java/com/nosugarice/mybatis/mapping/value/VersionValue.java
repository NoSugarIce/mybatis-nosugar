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

package com.nosugarice.mybatis.mapping.value;

import com.nosugarice.mybatis.util.Preconditions;
import com.nosugarice.mybatis.valuegenerator.PropertyParameter;
import com.nosugarice.mybatis.valuegenerator.VersionValueGenerator;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/6
 */
public class VersionValue extends SimpleValue<Serializable> {

    private static final long serialVersionUID = 8728506796783605330L;

    private static final Map<Class<?>, String> DEFAULT_VALUE = new HashMap<>();

    private static final String INITIAL_TIME = "1970-01-01 00:00:00";

    static {
        DEFAULT_VALUE.put(int.class, "0");
        DEFAULT_VALUE.put(Integer.class, "0");
        DEFAULT_VALUE.put(long.class, "0");
        DEFAULT_VALUE.put(Long.class, "0");
        DEFAULT_VALUE.put(Timestamp.class, INITIAL_TIME);
        DEFAULT_VALUE.put(Date.class, INITIAL_TIME);
        DEFAULT_VALUE.put(LocalDateTime.class, INITIAL_TIME);
    }

    /** 乐观锁字段类型 */
    private final Class<?> columnType;

    public VersionValue(Class<?> columnType) {
        Preconditions.checkArgument(DEFAULT_VALUE.containsKey(columnType), true, "版本号字段不支持类型:" + columnType);
        this.columnType = columnType;
    }

    @Override
    public String getDefaultValue() {
        return DEFAULT_VALUE.get(columnType);
    }

    public Serializable nextValue(Object value) {
        PropertyParameter parameter = new PropertyParameter((Serializable) value);
        return VersionValueGenerator.getInstance().generateValue(parameter);
    }

}
