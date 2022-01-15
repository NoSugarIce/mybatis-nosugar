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

package com.nosugarice.mybatis.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public class ParameterBind implements Cloneable {

    private static final String PARAMETER_PREFIX = "p";

    private final AtomicInteger seq = new AtomicInteger();

    private List<ParameterColumnBind> parameterColumnBinds = new ArrayList<>();

    public ParameterColumnBind bindValue(Object value, String column, Class<?> entityClass) {
        String parameterPlaceholder = nextParameterPlaceholder(column);
        ParameterColumnBind parameterColumnBind = new ParameterColumnBind(parameterPlaceholder, column, value, entityClass);
        parameterColumnBinds.add(parameterColumnBind);
        return parameterColumnBind;
    }

    private String nextParameterPlaceholder(String column) {
        return PARAMETER_PREFIX + seq.getAndIncrement() + column;
    }

    @Override
    public ParameterBind clone() {
        try {
            ParameterBind clone = (ParameterBind) super.clone();
            clone.parameterColumnBinds = parameterColumnBinds.stream().map(ParameterColumnBind::clone).collect(Collectors.toList());
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static class ParameterColumnBind implements Cloneable {
        private final String parameter;
        private final String column;
        private final Object value;
        private final Class<?> entityClass;
        private boolean canHandle;

        private ParameterColumnBind(String parameter, String column, Object value, Class<?> entityClass) {
            this.parameter = parameter;
            this.column = column;
            this.value = value;
            this.entityClass = entityClass;
        }

        public void canHandle() {
            this.canHandle = true;
        }

        public String getParameter() {
            return parameter;
        }

        public String getColumn() {
            return column;
        }

        public Object getValue() {
            return value;
        }

        public Class<?> getEntityClass() {
            return entityClass;
        }

        public boolean isCanHandle() {
            return canHandle;
        }

        @Override
        public ParameterColumnBind clone() {
            try {
                return (ParameterColumnBind) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ParameterColumnBind that = (ParameterColumnBind) o;
            return canHandle == that.canHandle
                    && Objects.equals(parameter, that.parameter)
                    && Objects.equals(column, that.column) && Objects.equals(value, that.value)
                    && Objects.equals(entityClass, that.entityClass);
        }

        @Override
        public int hashCode() {
            return Objects.hash(parameter, column, value, entityClass, canHandle);
        }
    }

    public List<ParameterColumnBind> getParameterColumnBinds() {
        return parameterColumnBinds;
    }

}
