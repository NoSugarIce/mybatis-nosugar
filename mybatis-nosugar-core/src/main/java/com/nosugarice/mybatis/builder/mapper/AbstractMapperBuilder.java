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

package com.nosugarice.mybatis.builder.mapper;

import com.nosugarice.mybatis.config.EntityMetadata;
import com.nosugarice.mybatis.config.MetadataBuildingContext;
import com.nosugarice.mybatis.config.OrderComparator;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/5
 */
public abstract class AbstractMapperBuilder implements OrderComparator {

    protected MetadataBuildingContext buildingContext;
    protected Configuration configuration;
    protected Class<?> mapperClass;
    protected EntityMetadata entityMetadata;

    private static final Map<Class<?>, List<Method>> MAPPER_METHOD_CACHE = new ConcurrentHashMap<>();

    public AbstractMapperBuilder withBuilding(MetadataBuildingContext buildingContext, Class<?> mapperClass) {
        this.buildingContext = buildingContext;
        this.mapperClass = mapperClass;
        this.configuration = buildingContext.getConfiguration();
        this.entityMetadata = buildingContext.getEntityMetadataByMapper(mapperClass);
        return this;
    }

    public void parse() {
        getMapperMethod(mapperClass).stream().filter(this::supportMethod).forEach(this::process);
    }

    /**
     * 是否合规Mapper接口
     *
     * @param mapperType
     * @return
     */
    public abstract boolean supportMapper(Class<?> mapperType);

    /**
     * 是否需要构建
     *
     * @param method
     * @return
     */
    public abstract boolean supportMethod(Method method);

    /**
     * 处理方法
     *
     * @param method
     */
    public abstract void process(Method method);

    protected String getMethodMappedStatementId(Method method) {
        return mapperClass.getName() + "." + method.getName();
    }

    protected boolean notHasStatement(Method method) {
        return !configuration.hasStatement(getMethodMappedStatementId(method));
    }

    private List<Method> getMapperMethod(Class<?> mapperClass) {
        return MAPPER_METHOD_CACHE.computeIfAbsent(mapperClass, clazz -> Arrays.stream(clazz.getMethods())
                .filter(method -> Modifier.isAbstract(method.getModifiers()))
                .filter(method -> !method.isBridge())
                .collect(Collectors.toList()));
    }

}
