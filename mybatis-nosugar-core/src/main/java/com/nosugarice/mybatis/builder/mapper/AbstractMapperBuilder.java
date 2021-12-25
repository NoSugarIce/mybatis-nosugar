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

package com.nosugarice.mybatis.builder.mapper;

import com.nosugarice.mybatis.annotation.Provider;
import com.nosugarice.mybatis.config.EntityMetadata;
import com.nosugarice.mybatis.config.MetadataBuildingContext;
import com.nosugarice.mybatis.config.OrderComparator;
import com.nosugarice.mybatis.util.Preconditions;
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
public abstract class AbstractMapperBuilder<T extends AbstractMapperBuilder<T>> implements OrderComparator {

    protected MetadataBuildingContext buildingContext;
    protected Configuration configuration;
    protected Class<?> mapperClass;
    protected EntityMetadata entityMetadata;

    private boolean isMapper;

    private static final Map<Class<?>, List<Method>> MAPPER_METHOD_CACHE = new WeakHashMap<>();

    public T withBuildingContext(MetadataBuildingContext buildingContext) {
        this.buildingContext = buildingContext;
        return getThis();
    }

    public T withMapper(Class<?> mapperClass) {
        this.mapperClass = mapperClass;
        return getThis();
    }

    @SuppressWarnings("unchecked")
    protected T getThis() {
        return (T) this;
    }

    public T build() {
        Preconditions.checkNotNull(buildingContext, "缺少MetadataBuildingContext!");
        Preconditions.checkNotNull(mapperClass, "缺少MapperInterface!");

        this.isMapper = isMapper();
        if (this.isMapper) {
            this.configuration = buildingContext.getConfiguration();
            this.entityMetadata = buildingContext.getEntityMetadataByMapper(mapperClass);
        }
        return getThis();
    }

    public void parse() {
        if (!isMapper) {
            return;
        }
        List<Method> needAchieveMapperMethods = needParseMethods();
        needAchieveMapperMethods.forEach(this::checkBeforeProcessMethod);
        needAchieveMapperMethods.forEach(this::processMethod);
    }

    /**
     * 是否合规Mapper接口
     *
     * @return
     */
    public abstract boolean isMapper();

    /**
     * 是否需要构建
     *
     * @param method
     * @return
     */
    public abstract boolean isCrudMethod(Method method);

    /**
     * 获取需要实现的方法
     *
     * @return
     */
    public List<Method> needParseMethods() {
        return getMapperMethod(mapperClass)
                .stream()
                .filter(this::isCrudMethod)
                .filter(this::isRepository)
                .collect(Collectors.toList());
    }

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

    public boolean notHasStatement(Method method) {
        return !configuration.hasStatement(getMethodMappedStatementId(method));
    }

    private boolean isRepository(Method method) {
        Provider annotation = method.getAnnotation(Provider.class);
        if (annotation != null) {
            return annotation.isProvider();
        }
        return true;
    }

    public String getMethodMappedStatementId(Method method) {
        return mapperClass.getName() + "." + method.getName();
    }

    public List<Method> getMapperMethod(Class<?> mapperClass) {
        return MAPPER_METHOD_CACHE.computeIfAbsent(mapperClass, clazz -> Arrays.stream(clazz.getMethods())
                .filter(method -> method.getDeclaringClass().isInterface())
                .filter(method -> Modifier.isAbstract(method.getModifiers()))
                .filter(method -> !method.isBridge())
                .collect(Collectors.toList()));
    }

}
