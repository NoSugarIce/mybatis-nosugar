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

package com.nosugarice.mybatis.builder;

import com.nosugarice.mybatis.annotation.Provider;
import com.nosugarice.mybatis.config.MetadataBuildingContext;
import com.nosugarice.mybatis.mapper.function.Mapper;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/5
 */
public abstract class AbstractMapperBuilder {

    protected final MetadataBuildingContext buildingContext;
    protected final Class<?> mapperInterface;
    protected final Configuration configuration;

    private static final Map<Class<?>, List<Method>> MAPPER_METHOD_CACHE = new WeakHashMap<>();

    AbstractMapperBuilder(MetadataBuildingContext buildingContext, Class<?> mapperInterface) {
        this.buildingContext = buildingContext;
        this.mapperInterface = mapperInterface;
        this.configuration = buildingContext.getConfiguration();
    }

    public void parse() {
        List<Method> needAchieveMapperMethods = needAchieveMapperMethod();
        needAchieveMapperMethods.forEach(this::checkBeforeProcessMethod);
        needAchieveMapperMethods.forEach(this::processMethod);
    }

    /**
     * 获取需要实现的方法
     *
     * @return
     */
    public List<Method> needAchieveMapperMethod() {
        List<Method> mapperMethod = getMapperMethod(mapperInterface);
        return mapperMethod.stream()
                .filter(this::isNeedAchieveMethod)
                .filter(this::isRepository)
                .collect(Collectors.toList());
    }

    /**
     * 是否需要构建
     *
     * @param method
     * @return
     */
    public abstract boolean isNeedAchieveMethod(Method method);

    /**
     * 处理方法前检查
     *
     * @param method
     */
    public abstract void checkBeforeProcessMethod(Method method);

    /**
     * 处理方法
     *
     * @param method
     */
    public abstract void processMethod(Method method);

    public boolean hasStatement(Method method) {
        return configuration.hasStatement(getMethodMappedStatementId(method));
    }

    private boolean isRepository(Method method) {
        Provider annotation = method.getAnnotation(Provider.class);
        if (annotation != null) {
            return annotation.isProvider();
        }
        return true;
    }

    public String getMethodMappedStatementId(Method method) {
        return mapperInterface.getName() + "." + method.getName();
    }

    public List<Method> getMapperMethod(Class<?> mapperClass) {
        return MAPPER_METHOD_CACHE.computeIfAbsent(mapperClass, aClass -> Arrays.stream(aClass.getMethods())
                .filter(method -> method.getDeclaringClass().isInterface())
                .filter(method -> Mapper.class.isAssignableFrom(method.getDeclaringClass()))
                .filter(method -> Modifier.isAbstract(method.getModifiers()))
                .filter(method -> !method.isBridge())
                .collect(Collectors.toList()));
    }

}
