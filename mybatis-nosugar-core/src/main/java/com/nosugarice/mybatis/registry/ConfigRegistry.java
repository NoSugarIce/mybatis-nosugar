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

package com.nosugarice.mybatis.registry;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/6/27
 */
public class ConfigRegistry {

    private final Map<Class<?>, Object> configMap = new HashMap<>();

    public void register(Object config) {
        if (config == null) {
            return;
        }
        configMap.put(config.getClass(), config);
    }

    @SuppressWarnings("unchecked")
    public <T> T getConfig(Class<T> configType) {
        return (T) configMap.get(configType);
    }

}
