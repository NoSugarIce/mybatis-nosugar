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

package com.nosugarice.mybatis.builder.sql;

import com.nosugarice.mybatis.builder.MapperMetadata;
import com.nosugarice.mybatis.config.MetadataBuildingContext;
import com.nosugarice.mybatis.sql.SqlProvider;
import com.nosugarice.mybatis.sql.SqlTempLateService;
import com.nosugarice.mybatis.util.Preconditions;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/2
 */
public class SqlScriptBuilder {

    private final Map<Method, SqlProvider> sqlSourceBuilderMap = new HashMap<>();

    private final MetadataBuildingContext buildingContext;
    private final Class<?> mapperInterface;
    private final Configuration configuration;
    private final MapperMetadata mapperMetadata;
    private final EntitySqlPart entitySqlPart;
    private final SqlTempLateService sqlTempLate;
    private final LanguageDriver languageDriver;

    public SqlScriptBuilder(Class<?> mapperInterface, MetadataBuildingContext buildingContext) {
        this.buildingContext = buildingContext;
        this.mapperInterface = mapperInterface;
        this.configuration = buildingContext.getConfiguration();
        this.mapperMetadata = buildingContext.getMapperMetadata(mapperInterface);
        this.entitySqlPart = new EntitySqlPart(this.mapperMetadata.getRelationalEntity(), this.mapperMetadata.getSupports()
                , this.mapperMetadata.getDialect(), true);
        this.sqlTempLate = new SqlTempLateServiceImpl(this.mapperMetadata, this.entitySqlPart);
        this.languageDriver = getMuLanguageDriver();
    }

    public void bind(Method method, SqlProvider sqlProvider) {
        sqlSourceBuilderMap.put(method, sqlProvider);
    }

    public String build(Method method) {
        SqlProvider sqlProvider = sqlSourceBuilderMap.get(method);
        Preconditions.checkNotNull(sqlProvider, "[" + method.getDeclaringClass() + "." + method.getName() + "]"
                + "没有找到构建sql的实现!");
        String sql = sqlProvider.provide(sqlTempLate);
        return SqlProvider.script(sql);
    }

    private LanguageDriver getMuLanguageDriver() {
        LanguageDriver driver = configuration.getLanguageRegistry().getDriver(XMLLanguageDriver.class);
        if (driver == null) {
            driver = new XMLLanguageDriver();
            configuration.getLanguageRegistry().register(driver);
        }
        return configuration.getLanguageDriver(XMLLanguageDriver.class);
    }

    public MetadataBuildingContext getBuildingContext() {
        return buildingContext;
    }

    public Class<?> getMapperInterface() {
        return mapperInterface;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public MapperMetadata getMapperBuildRaw() {
        return mapperMetadata;
    }

    public EntitySqlPart getEntitySqlPart() {
        return entitySqlPart;
    }

    public SqlTempLateService getDynamicSqlTempLate() {
        return sqlTempLate;
    }

    public LanguageDriver getLanguageDriver() {
        return languageDriver;
    }
}
