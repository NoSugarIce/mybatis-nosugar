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

package com.nosugarice.mybatis.annotation;

import com.nosugarice.mybatis.handler.ValueHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.io.Serializable;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 列其他选项
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/12/6
 */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnOptions {

    boolean ignoreEmptyChar() default false;

    Class<? extends TypeHandler<?>> typeHandler() default VoidHandler.class;

    Class<? extends ValueHandler<? extends Serializable>> insertHandler() default VoidHandler.class;

    Class<? extends ValueHandler<? extends Serializable>> updateHandler() default VoidHandler.class;

    Class<? extends ValueHandler<? extends Serializable>> logicDeleteHandler() default VoidHandler.class;

    Class<? extends ValueHandler<? extends Serializable>> resultHandler() default VoidHandler.class;

    class VoidHandler implements TypeHandler<Void>, ValueHandler<VoidHandler.VoidS> {

        @Override
        public void setParameter(PreparedStatement ps, int i, Void parameter, JdbcType jdbcType) {
        }

        @Override
        public Void getResult(ResultSet rs, String columnName) {
            return null;
        }

        @Override
        public Void getResult(ResultSet rs, int columnIndex) {
            return null;
        }

        @Override
        public Void getResult(CallableStatement cs, int columnIndex) {
            return null;
        }

        @Override
        public VoidS setValue(VoidS value) {
            return null;
        }

        public static class VoidS implements Serializable {
            private static final long serialVersionUID = -7262888801476099994L;
        }
    }

}
