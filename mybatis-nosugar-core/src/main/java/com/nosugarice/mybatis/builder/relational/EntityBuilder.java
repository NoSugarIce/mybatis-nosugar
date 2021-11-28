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

package com.nosugarice.mybatis.builder.relational;

import com.nosugarice.mybatis.config.RelationalConfig;
import com.nosugarice.mybatis.handler.ValueHandler;
import com.nosugarice.mybatis.mapping.RelationalEntity;
import com.nosugarice.mybatis.registry.BeanRegistry;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/6/12
 */
public abstract class EntityBuilder {

    protected Class<?> entityClass;
    protected RelationalConfig config;
    protected BeanRegistry<ValueHandler<?>> valueHandlerRegistry;

    public EntityBuilder withEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
        return this;
    }

    public EntityBuilder withRelationalConfig(RelationalConfig config) {
        this.config = config;
        return this;
    }

    public EntityBuilder withValueHandlerRegistry(BeanRegistry<ValueHandler<?>> valueHandlerRegistry) {
        this.valueHandlerRegistry = valueHandlerRegistry;
        return this;
    }

    /**
     * 构建RelationalEntity
     *
     * @return
     */
    public abstract RelationalEntity build();
}
