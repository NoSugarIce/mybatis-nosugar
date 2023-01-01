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

package com.nosugarice.mybatis.config;

import com.nosugarice.mybatis.builder.mapper.AbstractMapperBuilder;
import com.nosugarice.mybatis.util.Preconditions;

import java.util.HashSet;
import java.util.Set;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/6
 */
public class SwitchConfig {

    /** 开启的功能 */
    private final Set<Class<? extends AbstractMapperBuilder>> mapperBuilders = new HashSet<>();

    /** 逻辑删除开关 */
    private boolean logicDelete = true;

    /** 乐观锁开关 */
    private boolean version = true;

    /** 懒加载 */
    private boolean lazyBuilder;

    /** 批量增强 */
    private boolean speedBatch = true;

    @SuppressWarnings("unchecked")
    public void includeMapperBuilder(Class<?> mapperBuilderClass) {
        Preconditions.checkArgument(AbstractMapperBuilder.class.isAssignableFrom(mapperBuilderClass)
                , mapperBuilderClass.getName() + "类型不正确!");
        mapperBuilders.add((Class<? extends AbstractMapperBuilder>) mapperBuilderClass);
    }

    public void excludeMapperBuilder(Class<?> mapperBuilderClass) {
        mapperBuilders.remove(mapperBuilderClass);
    }

    public Set<Class<? extends AbstractMapperBuilder>> getMapperBuilders() {
        return mapperBuilders;
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
