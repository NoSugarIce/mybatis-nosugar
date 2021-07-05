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

package com.nosugarice.mybatis.builder.mybatis;

import com.nosugarice.mybatis.annotation.SpeedBatch;
import org.apache.ibatis.annotations.Flush;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/8
 */
public class MutativeMapperProxy<T> implements InvocationHandler {

    private final Class<T> mapperInterface;
    private final T defaultMapperProxy;
    private final T batchMapperProxy;

    public MutativeMapperProxy(Class<T> mapperInterface, T defaultMapperProxy, T batchMapperProxy) {
        this.mapperInterface = mapperInterface;
        this.defaultMapperProxy = defaultMapperProxy;
        this.batchMapperProxy = batchMapperProxy != null ? batchMapperProxy : defaultMapperProxy;
    }

    @SuppressWarnings("unchecked")
    public T getObject() {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isAnnotationPresent(SpeedBatch.class) || method.isAnnotationPresent(Flush.class)) {
            return method.invoke(batchMapperProxy, args);
        } else {
            return method.invoke(defaultMapperProxy, args);
        }
    }

}
