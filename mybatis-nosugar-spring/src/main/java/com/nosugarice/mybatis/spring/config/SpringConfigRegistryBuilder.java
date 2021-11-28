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

package com.nosugarice.mybatis.spring.config;

import com.nosugarice.mybatis.config.MapperBuilderConfig;
import com.nosugarice.mybatis.config.RelationalConfig;
import com.nosugarice.mybatis.config.SqlBuildConfig;
import com.nosugarice.mybatis.config.SwitchConfig;
import com.nosugarice.mybatis.registry.ConfigRegistry;
import com.nosugarice.mybatis.util.Preconditions;
import org.springframework.context.ApplicationContext;

import java.util.Collection;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/6/27
 */
public class SpringConfigRegistryBuilder {

    private final ApplicationContext applicationContext;

    public SpringConfigRegistryBuilder(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ConfigRegistry build() {
        ConfigRegistry registry = new ConfigRegistry();
        registry.register(getConfig(MapperBuilderConfig.class));
        registry.register(getConfig(SwitchConfig.class));
        registry.register(getConfig(RelationalConfig.class));
        registry.register(getConfig(SqlBuildConfig.class));
        return registry;
    }

    private <T> T getConfig(Class<T> clazz) {
        T bean = null;
        if (applicationContext != null) {
            Collection<T> beans = applicationContext.getBeansOfType(clazz).values();
            if (!beans.isEmpty()) {
                Preconditions.checkArgument(beans.size() <= 1, "多个[" + clazz.getName() + "]定义!");
                bean = beans.iterator().next();
            }
        }
        return bean;
    }

}
