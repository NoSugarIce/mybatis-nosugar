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

import com.nosugarice.mybatis.builder.MapperMetadata;
import com.nosugarice.mybatis.builder.relational.AbstractEntityBuilder;
import com.nosugarice.mybatis.builder.relational.DefaultEntityBuilder;
import com.nosugarice.mybatis.builder.sql.SqlScriptBuilder;
import com.nosugarice.mybatis.data.ReservedWords;
import com.nosugarice.mybatis.dialect.Dialect;
import com.nosugarice.mybatis.dialect.DialectRegistry;
import com.nosugarice.mybatis.mapper.function.MapperHelp;
import com.nosugarice.mybatis.mapping.RelationalEntity;
import com.nosugarice.mybatis.reflection.EntityClass;
import com.nosugarice.mybatis.util.ReflectionUtils;
import com.nosugarice.mybatis.valuegenerator.id.IdGenerator;
import com.nosugarice.mybatis.valuegenerator.id.IdGeneratorRegistry;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/11/22
 */
public class MetadataBuildingContext {

    private final Configuration configuration;
    private final MapperBuilderConfig config;
    private final Dialect dialect;
    private final IdGeneratorRegistry idGeneratorRegistry = new IdGeneratorRegistry();
    private final Map<Class<?>, MapperMetadata> mapperBuildRawMap = new HashMap<>();
    private final Map<Class<?>, SqlScriptBuilder> mapperSqlScriptBuilderMap = new HashMap<>();
    private final Map<Class<?>, MapperBuilderAssistant> mapperMapperBuilderAssistantMap = new HashMap<>();

    public MetadataBuildingContext(Configuration configuration, MapperBuilderConfig config) {
        this.configuration = configuration;
        this.config = config;
        this.dialect = config.getSqlBuildConfig().getDialect() != null ? config.getSqlBuildConfig().getDialect() : getDialect(configuration);
        registryIdGenerator(config.getRelationalConfig().getIdGenerators());
    }

    public MapperMetadata getMapperMetadata(Class<?> mapperInterface) {
        return mapperBuildRawMap.computeIfAbsent(mapperInterface, interfaceClass -> {
            Class<?> entityClass = MapperHelp.getMapperService().analyzeEntityClass(mapperInterface);
            Class<? extends AbstractEntityBuilder<?>> entityBuilderType = config.getRelationalConfig().getEntityBuilderType();
            entityBuilderType = entityBuilderType == null ? DefaultEntityBuilder.class : entityBuilderType;
            AbstractEntityBuilder<?> entityBuilder = ReflectionUtils.newInstance(entityBuilderType);
            RelationalEntity relationalEntity = entityBuilder
                    .withEntityClass(new EntityClass(entityClass))
                    .withRelationalConfig(config.getRelationalConfig())
                    .build();

            return new MapperMetadata(interfaceClass, dialect, relationalEntity, config);
        });
    }

    public SqlScriptBuilder getSqlScriptBuilder(Class<?> mapperInterface) {
        return mapperSqlScriptBuilderMap.computeIfAbsent(mapperInterface
                , interfaceClass -> new SqlScriptBuilder(mapperInterface, this));
    }

    public MapperBuilderAssistant getMapperBuilderAssistant(Class<?> mapperInterface) {
        return mapperMapperBuilderAssistantMap.computeIfAbsent(mapperInterface
                , interfaceClass -> {
                    String resource = mapperInterface.getName().replace('.', '/') + ".java (best guess)";
                    MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, resource);
                    assistant.setCurrentNamespace(mapperInterface.getName());
                    return assistant;
                });
    }

    public void clearMapperBuildRaw(Class<?> mapperInterfaceClass) {
        mapperBuildRawMap.remove(mapperInterfaceClass);
        mapperSqlScriptBuilderMap.remove(mapperInterfaceClass);
        mapperMapperBuilderAssistantMap.remove(mapperInterfaceClass);
    }

    private Dialect getDialect(Configuration configuration) {
        String databaseName;
        try {
            Connection connection = configuration.getEnvironment().getDataSource().getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            Arrays.stream(metaData.getSQLKeywords().split(",")).forEach(ReservedWords.SQL::registerKeyword);
            databaseName = metaData.getDatabaseProductName();
        } catch (SQLException e) {
            throw new BuilderException(e);
        }
        //TODO 可能会有问题,没有一一验证
        return new DialectRegistry().getObject(databaseName);
    }

    private void registryIdGenerator(Map<String, IdGenerator<?>> idGenerators) {
        if (idGenerators == null) {
            return;
        }
        idGenerators.forEach(idGeneratorRegistry::register);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public MapperBuilderConfig getConfig() {
        return config;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public IdGeneratorRegistry getIdGeneratorRegistry() {
        return idGeneratorRegistry;
    }
}
