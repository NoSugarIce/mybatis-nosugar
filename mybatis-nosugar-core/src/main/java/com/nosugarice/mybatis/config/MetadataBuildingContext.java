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

import com.nosugarice.mybatis.assign.id.IdGenerator;
import com.nosugarice.mybatis.builder.MapperBuilder;
import com.nosugarice.mybatis.dialect.Dialect;
import com.nosugarice.mybatis.dialect.DialectFactory;
import com.nosugarice.mybatis.dialect.RuntimeDialect;
import com.nosugarice.mybatis.handler.ValueHandler;
import com.nosugarice.mybatis.mapping.RelationalEntity;
import com.nosugarice.mybatis.registry.BeanRegistry;
import com.nosugarice.mybatis.registry.EntityMetadataRegistry;
import com.nosugarice.mybatis.registry.GenericHandlerRegistrar;
import com.nosugarice.mybatis.registry.IdGeneratorRegistry;
import com.nosugarice.mybatis.sqlsource.SqlSourceScriptBuilder;
import com.nosugarice.mybatis.util.Preconditions;
import com.nosugarice.mybatis.util.ReflectionUtils;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/11/22
 */
public class MetadataBuildingContext {

    private final Configuration configuration;
    private final MapperBuilderConfig config;
    private final MapperBuilder mapperBuilder;
    private final DialectFactory dialectFactory;
    private final Dialect dialect;
    private final BeanRegistry<IdGenerator<?>> idGeneratorRegistry;
    private final BeanRegistry<ValueHandler<?>> valueHandlerRegistry;
    private final GenericHandlerRegistrar genericHandlerRegistrar;
    private final Map<Class<?>, Class<?>> mapperEntityClassMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, EntityMetadata> entityMetadataMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, SqlSourceScriptBuilder> entitySqlScriptBuilderMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, MapperBuilderAssistant> mapperBuilderAssistantMap = new ConcurrentHashMap<>();

    public MetadataBuildingContext(Configuration configuration, MapperBuilderConfig config) {
        this.configuration = configuration;
        this.config = config;
        this.mapperBuilder = new MapperBuilder(this);
        this.dialectFactory = config.getSqlBuildConfig().getDialectFactory();

        Dialect defaultDialect = config.getSqlBuildConfig().getDialect();
        defaultDialect = defaultDialect != null
                ? defaultDialect : dialectFactory.getDialect(configuration.getEnvironment().getDataSource());
        DialectContext.defaultDialect = defaultDialect;

        this.dialect = config.getSqlBuildConfig().isRuntimeDialect() ? new RuntimeDialect() : defaultDialect;
        this.idGeneratorRegistry = new IdGeneratorRegistry();
        this.valueHandlerRegistry = new BeanRegistry<>();
        this.genericHandlerRegistrar = new GenericHandlerRegistrar();
        registryBean(config.getRelationalConfig());
    }

    public Class<?> getEntityClass(Class<?> mapperClass) {
        return mapperEntityClassMap.computeIfAbsent(mapperClass, clazz -> {
            Class<?> entityClass = config.getRelationalConfig().getMapperStrategy().analyzeEntityClass(clazz);
            Preconditions.checkNotNull(entityClass, "从[" + clazz.getSimpleName() + "]未获取到实体类信息!");
            return entityClass;
        });
    }

    public EntityMetadata getEntityMetadataByMapper(Class<?> mapperClass) {
        return getEntityMetadata(getEntityClass(mapperClass));
    }

    public EntityMetadata getEntityMetadata(Class<?> entityClass) {
        return entityMetadataMap.computeIfAbsent(entityClass, clazz -> {
            Class<? extends EntityBuilder> entityBuilderType = config.getRelationalConfig().getEntityBuilderType();
            EntityBuilder entityBuilder = ReflectionUtils.newInstance(entityBuilderType);
            RelationalEntity relationalEntity = entityBuilder
                    .withEntityClass(clazz)
                    .withRelationalConfig(config.getRelationalConfig())
                    .withValueHandlerRegistry(valueHandlerRegistry)
                    .build();
            EntityMetadata entityMetadata = new EntityMetadata(relationalEntity, config);
            EntityMetadataRegistry metadataRegistry = EntityMetadataRegistry.getInstance();
            metadataRegistry.register(entityClass, entityMetadata);
            return entityMetadata;
        });
    }

    public SqlSourceScriptBuilder getSqlScriptBuilderByMapper(Class<?> mapperClass) {
        return getSqlScriptBuilder(getEntityClass(mapperClass));
    }

    public SqlSourceScriptBuilder getSqlScriptBuilder(Class<?> entityClass) {
        return entitySqlScriptBuilderMap.computeIfAbsent(entityClass
                , clazz -> {
                    EntityMetadata entityMetadata = getEntityMetadata(clazz);
                    return new SqlSourceScriptBuilder(entityMetadata, this);
                });
    }

    public MapperBuilderAssistant getMapperBuilderAssistant(Class<?> mapperClass) {
        return mapperBuilderAssistantMap.computeIfAbsent(mapperClass
                , clazz -> {
                    String resource = clazz.getName().replace('.', '/') + ".java (best guess)";
                    MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, resource);
                    assistant.setCurrentNamespace(clazz.getName());
                    return assistant;
                });
    }

    public void clearMapperBuildRaw(Class<?> mapperInterfaceClass) {
        mapperBuilderAssistantMap.remove(mapperInterfaceClass);
    }

    private void registryBean(RelationalConfig relationalConfig) {
        Map<String, IdGenerator<?>> idGenerators;
        if ((idGenerators = relationalConfig.getIdGenerators()) != null) {
            idGenerators.forEach(idGeneratorRegistry::register);
        }
        List<ValueHandler<?>> valueHandlers;
        if ((valueHandlers = relationalConfig.getValueHandlers()) != null) {
            valueHandlers.forEach(valueHandlerRegistry::register);
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public MapperBuilderConfig getConfig() {
        return config;
    }

    public MapperBuilder getMapperBuilder() {
        return mapperBuilder;
    }

    public DialectFactory getDialectFactory() {
        return dialectFactory;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public BeanRegistry<IdGenerator<?>> getIdGeneratorRegistry() {
        return idGeneratorRegistry;
    }

    public GenericHandlerRegistrar getGenericHandlerRegistrar() {
        return genericHandlerRegistrar;
    }
}
