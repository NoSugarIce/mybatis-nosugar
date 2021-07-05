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

package com.nosugarice.mybatis.reflection;

import com.nosugarice.mybatis.util.ReflectionUtils;

import javax.persistence.AccessType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * org.hibernate.annotations.common.reflection.java.JavaXClass
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/11/22
 */
public class EntityClass extends EntityAnnotatedElement {

    private final Class<?> clazz;

    private final List<EntityProperty> fieldProperties = new LinkedList<>();

    private final List<EntityProperty> methodProperties = new LinkedList<>();

    public EntityClass(Class<?> clazz) {
        super(clazz);
        this.clazz = clazz;
    }

    public Class<?> getClassType() {
        return clazz;
    }

    public String getName() {
        return clazz.getSimpleName();
    }

    public boolean isInterface() {
        return clazz.isInterface();
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    public boolean isPrimitive() {
        return clazz.isPrimitive();
    }

    public boolean isEnum() {
        return clazz.isEnum();
    }

    public List<EntityProperty> getDeclaredProperties(AccessType accessType) {
        if (accessType == AccessType.FIELD) {
            return getDeclaredFieldProperties();
        }
        if (accessType == AccessType.PROPERTY) {
            return getDeclaredMethodProperties();
        }
        throw new IllegalArgumentException("Unknown access type " + accessType);
    }

    private synchronized List<EntityProperty> getDeclaredFieldProperties() {
        if (fieldProperties.isEmpty()) {
            for (Field field : ReflectionUtils.getAllField(clazz)) {
                fieldProperties.add(new EntityProperty(field));
            }
        }
        return fieldProperties;
    }

    private synchronized List<EntityProperty> getDeclaredMethodProperties() {
        if (methodProperties.isEmpty()) {
            for (Method method : ReflectionUtils.getAllGetMethod(clazz)) {
                methodProperties.add(new EntityProperty(method));
            }
        }
        return methodProperties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntityClass that = (EntityClass) o;
        return Objects.equals(clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz);
    }

}
