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

package com.nosugarice.mybatis.util;

import com.nosugarice.mybatis.exception.NoSugarException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/11/22
 */
public class ReflectionUtils {

    /**
     * 判断方法是不是属性
     *
     * @param method 成员方法
     * @return
     */
    public static boolean isProperty(Method method) {
        return !method.isSynthetic()
                && !method.isBridge()
                && !Modifier.isStatic(method.getModifiers())
                && method.getParameterTypes().length == 0
                && (method.getName().startsWith("get") || method.getName().startsWith("is"));
    }

    /**
     * 判断字段是不是属性
     *
     * @param field 成员变量
     * @return
     */
    public static boolean isProperty(Field field) {
        return !Modifier.isStatic(field.getModifiers())
                && !Modifier.isTransient(field.getModifiers())
                && !field.isSynthetic();
    }

    /**
     * 获取所有字段
     *
     * @param clazz java类型
     * @return
     */
    public static List<Field> getAllField(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Set<String> fieldNames = new HashSet<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (ReflectionUtils.isProperty(field)) {
                fields.add(field);
                fieldNames.add(field.getName());
            }
        }
        Class<?> superClass = clazz;
        while ((superClass = superClass.getSuperclass()) != null) {
            for (Field field : superClass.getDeclaredFields()) {
                if (ReflectionUtils.isProperty(field)) {
                    if (!fieldNames.contains(field.getName())) {
                        fields.add(field);
                        fieldNames.add(field.getName());
                    }
                }
            }
        }
        return fields;
    }

    /**
     * 获取所有get方法
     *
     * @param clazz java类型
     * @return
     */
    public static List<Method> getAllGetMethod(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        Set<String> methodNames = new HashSet<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (ReflectionUtils.isProperty(method)) {
                methods.add(method);
                methodNames.add(method.getName());
            }
        }
        Class<?> superClass = clazz;
        while ((superClass = superClass.getSuperclass()) != null) {
            for (Method method : superClass.getDeclaredMethods()) {
                if (ReflectionUtils.isProperty(method)) {
                    if (!methodNames.contains(method.getName())) {
                        methods.add(method);
                        methodNames.add(method.getName());
                    }
                }
            }
        }
        return methods;
    }

    public static <T> T newInstance(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new NoSugarException("[" + clazz.getName() + "]未找到公共无参构造函数!");
        } catch (Exception e) {
            throw new NoSugarException(e);
        }
    }

    public static Class<?> getPropertyType(Member member) {
        return member instanceof Field ? ((Field) member).getType() : ((Method) member).getReturnType();
    }

    public static String getPropertyName(Member member) {
        if (member instanceof Method) {
            String fullName = member.getName();
            if (fullName.startsWith("get")) {
                return decapitalize(fullName.substring(3));
            }
            if (fullName.startsWith("is")) {
                return decapitalize(fullName.substring(2));
            }
            throw new NoSugarException("Method " + fullName + " is not a property getter");
        }
        return member.getName();
    }

    public static Object invoke(Method method, Object obj, Object... args) {
        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static String decapitalize(String name) {
        if (name != null && name.length() != 0) {
            if (name.length() > 1 && Character.isUpperCase(name.charAt(1))) {
                return name;
            } else {
                char[] chars = name.toCharArray();
                chars[0] = Character.toLowerCase(chars[0]);
                return new String(chars);
            }
        } else {
            return name;
        }
    }

}
