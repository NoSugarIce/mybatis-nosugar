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

import com.nosugarice.mybatis.config.ConfigRegistry;
import com.nosugarice.mybatis.config.MapperBuilderConfig;
import com.nosugarice.mybatis.config.MapperBuilderConfigBuilder;
import com.nosugarice.mybatis.config.MetadataBuildingContext;
import com.nosugarice.mybatis.mapper.function.Mapper;
import com.nosugarice.mybatis.mapper.function.MapperHelp;
import com.nosugarice.mybatis.support.MapperServiceImpl;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dingjingyang@foxmail.com(dingjingyang)
 * @date 2021/6/27
 */
public class NoSugarMapperBuilder {

    private static final Log LOG = LogFactory.getLog(NoSugarMapperBuilder.class);

    private static final Map<Configuration, MetadataBuildingContext> BUILDING_CONTEXT_MAP = new ConcurrentHashMap<>();

    private final Configuration configuration;
    private final Class<?> mapperInterface;
    private final ConfigRegistry configRegistry;

    private final MetadataBuildingContext metadataBuildingContext;
    private final MapperBuilderConfig mapperBuilderConfig;

    private boolean loaded;

    public NoSugarMapperBuilder(Configuration configuration, Class<?> mapperInterface, ConfigRegistry configRegistry) {
        this.configuration = configuration;
        this.mapperInterface = mapperInterface;
        this.configRegistry = configRegistry;
        this.metadataBuildingContext = initMetadataBuildingContext();
        this.mapperBuilderConfig = metadataBuildingContext.getConfig();
    }

    private MetadataBuildingContext initMetadataBuildingContext() {
        return BUILDING_CONTEXT_MAP.computeIfAbsent(configuration, configurationKey -> {
            MapperBuilderConfig mapperBuilderConfig = this.mapperBuilderConfig != null
                    ? this.mapperBuilderConfig : new MapperBuilderConfigBuilder(configRegistry, configurationKey.getVariables()).build();
            if (!MapperHelp.hasMapperService()) {
                MapperServiceImpl mapperService = new MapperServiceImpl();
                mapperService.setReflectorFactory(configuration.getReflectorFactory());
                MapperHelp.registerMapperService(mapperService);
            }
            return new MetadataBuildingContext(configurationKey, mapperBuilderConfig);
        });
    }

    public void process() {
        if (!Mapper.class.isAssignableFrom(mapperInterface)) {
            return;
        }
        if (loaded) {
            LOG.warn("[" + mapperInterface.getName() + "] " + " 基础查询配置已添加.");
            return;
        }
        boolean loadCompleted = false;
        try {
            //构建基本的curd
            if (mapperBuilderConfig.getSwitchConfig().isCrud()) {
                AbstractMapperBuilder mapperEntityClassBuilder = new MapperEntityStatementBuilder(metadataBuildingContext, mapperInterface);
                mapperEntityClassBuilder.parse();
            }

            //构建根据方法名构建相关查询方法
            if (mapperBuilderConfig.getSwitchConfig().isFindByMethodName()) {
                AbstractMapperBuilder mapperMethodNameBuilder = new MapperMethodNameBuilder(metadataBuildingContext, mapperInterface);
                mapperMethodNameBuilder.parse();
            }

            //构建count和分页相关方法
            if (mapperBuilderConfig.getSwitchConfig().isMutative()) {
                AbstractMapperBuilder mutativeSqlBuilder = new MutativeSqlBuilder(metadataBuildingContext, mapperInterface);
                mutativeSqlBuilder.parse();
            }
            loadCompleted = true;
        } finally {
            if (loadCompleted) {
                //构建之后清除部分后续用不到的数据
                metadataBuildingContext.clearMapperBuildRaw(mapperInterface);
                loaded = true;
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("[" + mapperInterface.getName() + "] " + "已构建");
        }
    }

    public MapperBuilderConfig getMapperBuilderConfig() {
        return mapperBuilderConfig;
    }

    public boolean isLoaded() {
        return loaded;
    }
}
