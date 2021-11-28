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

package com.nosugarice.mybatis.config;

import com.nosugarice.mybatis.builder.AbstractMapperBuilder;
import com.nosugarice.mybatis.util.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/6
 */
public class SwitchConfig {

    /** 开启的功能 */
    private final List<Class<? extends AbstractMapperBuilder<?>>> includeMapperBuilders = new ArrayList<>();

    /** 排除的功能 */
    private List<Class<?>> excludeMapperBuilders;

    /** 逻辑删除开关 */
    private boolean logicDelete = true;

    /** 乐观锁开关 */
    private boolean version = true;

    /** 懒加载 */
    private boolean lazyBuilder;

    /** 批量增强 */
    private boolean speedBatch = true;

    public Set<Class<? extends AbstractMapperBuilder<?>>> getMapperBuilders() {
        return includeMapperBuilders.stream()
                .filter(clazz -> excludeMapperBuilders == null || !excludeMapperBuilders.contains(clazz))
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    public void addIncludeMapperBuilder(Class<?> mapperBuilderClass) {
        Preconditions.checkArgument(AbstractMapperBuilder.class.isAssignableFrom(mapperBuilderClass)
                , mapperBuilderClass.getName() + "类型不正确!");
        includeMapperBuilders.add((Class<? extends AbstractMapperBuilder<?>>) mapperBuilderClass);
    }

    public void addExcludeMapperBuilder(Class<?> mapperBuilderClass) {
        if (excludeMapperBuilders == null) {
            excludeMapperBuilders = new ArrayList<>();
        }
        excludeMapperBuilders.add(mapperBuilderClass);
    }

    public boolean isLogicDelete() {
        return logicDelete;
    }

    public void setLogicDelete(boolean logicDelete) {
        this.logicDelete = logicDelete;
    }

    public boolean isVersion() {
        return version;
    }

    public void setVersion(boolean version) {
        this.version = version;
    }

    public boolean isLazyBuilder() {
        return lazyBuilder;
    }

    public void setLazyBuilder(boolean lazyBuilder) {
        this.lazyBuilder = lazyBuilder;
    }

    public boolean isSpeedBatch() {
        return speedBatch;
    }

    public void setSpeedBatch(boolean speedBatch) {
        this.speedBatch = speedBatch;
    }
}
