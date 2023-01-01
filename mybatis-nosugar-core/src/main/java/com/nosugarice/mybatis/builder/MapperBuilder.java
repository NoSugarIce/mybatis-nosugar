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

package com.nosugarice.mybatis.builder;

import com.nosugarice.mybatis.config.MetadataBuildingContext;
import com.nosugarice.mybatis.config.OrderComparator;
import com.nosugarice.mybatis.util.ReflectionUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/6/27
 */
public class MapperBuilder {

    private static final Log LOG = LogFactory.getLog(MapperBuilder.class);

    private final Set<Integer> mapperHasHCodes = new HashSet<>();
    private final MetadataBuildingContext buildingContext;

    public MapperBuilder(MetadataBuildingContext buildingContext) {
        this.buildingContext = buildingContext;
    }

    public void process(Class<?> mapperType) {
        boolean loadCompleted = false;
        try {
            buildingContext.getConfig().getSwitchConfig().getMapperBuilders().stream()
                    .map(ReflectionUtils::newInstance)
                    .sorted(OrderComparator::compareTo)
                    .filter(mapperBuilder -> mapperBuilder.supportMapper(mapperType))
                    .forEach(mapperBuilder -> mapperBuilder.withBuilding(buildingContext, mapperType).parse());
            loadCompleted = true;
        } finally {
            if (loadCompleted) {
                mapperHasHCodes.add(mapperType.hashCode());
                buildingContext.clearMapperBuildRaw(mapperType);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("[" + mapperType.getName() + "] " + "已构建");
        }
    }

    public boolean isLoaded(Class<?> mapperType) {
        return mapperHasHCodes.contains(mapperType.hashCode());
    }
}
