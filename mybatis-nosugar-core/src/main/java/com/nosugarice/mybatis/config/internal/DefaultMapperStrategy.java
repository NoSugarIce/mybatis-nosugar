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

package com.nosugarice.mybatis.config.internal;

import com.nosugarice.mybatis.config.MapperStrategy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public class DefaultMapperStrategy implements MapperStrategy {

    /**
     * 根据Mapper接口上定义的泛型类型,取第一个
     *
     * @param mapperInterface
     * @return
     */
    @Override
    public Class<?> analyzeEntityClass(Class<?> mapperInterface) {
        Type[] types = mapperInterface.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                if (((Class<?>) parameterizedType.getRawType()).isAssignableFrom(mapperInterface)) {
                    return (Class<?>) parameterizedType.getActualTypeArguments()[0];
                }
            }
        }
        return null;
    }

}
