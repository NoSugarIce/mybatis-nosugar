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

package com.nosugarice.mybatis.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.TreeSet;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public class ServiceLoaderUtils {

    public static <T> T loadSingleInstance(Class<T> clazz) {
        List<T> instances = loadInstances(clazz);
        if (instances.isEmpty()) {
            throw new IllegalArgumentException("当前接口没有SPI实现.");
        }
        return instances.get(0);
    }

    public static <T> List<T> loadInstances(Class<T> clazz) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
        List<T> list = new ArrayList<>();
        boolean isComparator = true;
        for (T t : serviceLoader) {
            list.add(t);
            if (isComparator) {
                isComparator = t instanceof Comparable;
            }
        }
        return isComparator ? new ArrayList<>(new TreeSet<>(list)) : list;
    }

}
