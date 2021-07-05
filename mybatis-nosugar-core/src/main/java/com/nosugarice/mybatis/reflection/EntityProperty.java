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

import com.nosugarice.mybatis.exception.NoSugarException;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * org.hibernate.annotations.common.reflection.java.JavaXProperty
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/11/22
 */
public class EntityProperty extends EntityAnnotatedElement {

    private final Member member;

    public EntityProperty(Member member) {
        super((AnnotatedElement) member);
        if (!Field.class.isAssignableFrom(member.getClass()) && !Method.class.isAssignableFrom(member.getClass())) {
            throw new IllegalArgumentException("type is not Field or Method");
        }
        this.member = member;
    }

    public Class<?> getClassType() {
        if (member instanceof Method) {
            return ((Method) member).getReturnType();
        } else {
            return ((Field) member).getType();
        }
    }

    public String getFullName() {
        return member.toString();
    }

    public String getName() {
        final String get = "get";
        String fullName = member.getName();
        if (member instanceof Method) {
            if (fullName.startsWith(get)) {
                return decapitalize(fullName.substring(get.length()));
            }
            if (fullName.startsWith("is")) {
                return decapitalize(fullName.substring("is".length()));
            }
            throw new NoSugarException("Method " + fullName + " is not a property getter");
        } else {
            return fullName;
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
