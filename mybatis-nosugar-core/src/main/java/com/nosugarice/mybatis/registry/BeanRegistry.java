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

package com.nosugarice.mybatis.registry;

import com.nosugarice.mybatis.util.Preconditions;
import com.nosugarice.mybatis.util.ReflectionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/6/12
 */
public class BeanRegistry<T> {

    private final Map<String, Class<? extends T>> typeMap = new ConcurrentHashMap<>();
    private final Map<String, T> objMap = new ConcurrentHashMap<>();

    /**
     * 注册类型
     *
     * @param clazz
     */
    public void registerType(Class<? extends T> clazz) {
        registerType(clazz.getName(), clazz);
    }

    /**
     * 注册类型
     *
     * @param name
     * @param clazz
     */
    public void registerType(String name, Class<? extends T> clazz) {
        if (!typeMap.containsKey(name)) {
            typeMap.put(name.toUpperCase(), clazz);
        }
    }

    /**
     * 注册对象
     *
     * @param obj
     */
    public void register(T obj) {
        if (obj != null) {
            register(obj.getClass().getName(), obj);
        }
    }

    /**
     * 注册对象
     *
     * @param name
     * @param obj
     */
    public void register(String name, T obj) {
        if (obj != null) {
            objMap.put(name.toUpperCase(), obj);
        }
    }

    public boolean containsType(String name) {
        return typeMap.containsKey(name.toUpperCase());
    }

    public T getObject(String name) {
        return objMap.computeIfAbsent(name.toUpperCase(), nameTemp -> {
            Class<? extends T> clazz = typeMap.get(nameTemp);
            Preconditions.checkNotNull(clazz, "标识为:[" + nameTemp + "]对应类型未注册!");
            return ReflectionUtils.newInstance(clazz);
        });
    }

    public T getObject(Class<? extends T> clazz) {
        return getObject(clazz.getName());
    }

}
