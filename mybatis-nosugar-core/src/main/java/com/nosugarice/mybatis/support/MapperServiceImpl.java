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

package com.nosugarice.mybatis.support;

import com.nosugarice.mybatis.builder.sql.MetadataCache;
import com.nosugarice.mybatis.mapper.function.MapperService;
import com.nosugarice.mybatis.util.CollectionUtils;
import com.nosugarice.mybatis.util.Preconditions;
import org.apache.ibatis.reflection.ReflectionException;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.invoker.Invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/6/12
 */
public class MapperServiceImpl implements MapperService {

    /** 初始化时必须赋值 */
    private ReflectorFactory reflectorFactory;

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

    @Override
    public <T> boolean entityIsNew(T entity) {
        List<MetadataCache.PropertyCache> columnCaches = MetadataCache.getPropertyCaches(entity.getClass());
        List<MetadataCache.PropertyCache> primaryKeyProperties = columnCaches.stream()
                .filter(MetadataCache.PropertyCache::isPrimaryKey)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(primaryKeyProperties)) {
            primaryKeyProperties = columnCaches;
        }
        for (MetadataCache.PropertyCache propertyCache : primaryKeyProperties) {
            Reflector reflector = reflectorFactory.findForClass(entity.getClass());
            Invoker getInvoker = reflector.getGetInvoker(propertyCache.getProperty());
            Preconditions.checkNotNull(getInvoker, "未找到属性[" + propertyCache.getProperty() + "]get方法");
            try {
                Object value = getInvoker.invoke(entity, null);
                if (value == null) {
                    return false;
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new ReflectionException("属性[" + propertyCache.getProperty() + "]get方法执行异常", e);
            }
        }
        return true;
    }

    public void setReflectorFactory(ReflectorFactory reflectorFactory) {
        this.reflectorFactory = reflectorFactory;
    }
}
