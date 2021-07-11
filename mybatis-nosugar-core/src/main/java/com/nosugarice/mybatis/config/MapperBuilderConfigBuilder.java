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

import com.nosugarice.mybatis.builder.relational.AbstractEntityBuilder;
import com.nosugarice.mybatis.builder.relational.DefaultEntityBuilder;
import com.nosugarice.mybatis.dialect.Dialect;
import com.nosugarice.mybatis.exception.NoSugarException;
import com.nosugarice.mybatis.support.NameStrategy;
import com.nosugarice.mybatis.support.NameStrategyType;
import com.nosugarice.mybatis.util.Preconditions;
import com.nosugarice.mybatis.util.ReflectionUtils;
import com.nosugarice.mybatis.util.StringFormatter;
import com.nosugarice.mybatis.util.StringUtils;
import com.nosugarice.mybatis.valuegenerator.id.IdGenerator;

import javax.persistence.AccessType;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 注册到Spring 环境中的配置优先于 Properties中配置
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/12/30
 */
public class MapperBuilderConfigBuilder {

    private static final String SWITCH_CONFIG = "mybatis.no-sugar.switch.";
    private static final String SWITCH_CONFIG_CRUD = SWITCH_CONFIG + "crud";
    private static final String SWITCH_CONFIG_FIND_BY_METHODNAME = SWITCH_CONFIG + "find-by-methodName";
    private static final String SWITCH_CONFIG_MUTATIVE = SWITCH_CONFIG + "mutative";
    private static final String SWITCH_CONFIG_LOGIC_DELETE = SWITCH_CONFIG + "logic-delete";
    private static final String SWITCH_CONFIG_VERSION = SWITCH_CONFIG + "version";
    private static final String SWITCH_CONFIG_LAZY_BUILDER = SWITCH_CONFIG + "lazy-builder";
    private static final String SWITCH_CONFIG_SPEED_BATCH = SWITCH_CONFIG + "speed-batch";

    private static final String RELATIONAL_CONFIG = "mybatis.no-sugar.relational.";
    private static final String RELATIONAL_CONFIG_ENTITY_BUILDER_CLASS = RELATIONAL_CONFIG + "entity-builder-class";
    private static final String RELATIONAL_CONFIG_ACCESS_TYPE = RELATIONAL_CONFIG + "access-type";
    private static final String RELATIONAL_CONFIG_CLASS_NAME_TO_TABLE_NAME_STRATEGY = RELATIONAL_CONFIG + "class-name-to-table-name-strategy";
    private static final String RELATIONAL_CONFIG_CLASS_NAME_TO_TABLE_NAME_STRATEGY_CLASS = RELATIONAL_CONFIG + "class-name-to-table-name-strategy-class";
    private static final String RELATIONAL_CONFIG_FIELD_NAME_TO_COLUMN_NAME_STRATEGY = RELATIONAL_CONFIG + "field-name-to-column-name-strategy";
    private static final String RELATIONAL_CONFIG_FIELD_NAME_TO_COLUMN_NAME_STRATEGY_CLASS = RELATIONAL_CONFIG + "field-name-to-column-name-strategy-class";
    private static final String RELATIONAL_CONFIG_JAVAX_VALIDATION_MAPPING_NOT_NULL = RELATIONAL_CONFIG + "javax-validation-mapping-not-null";
    private static final String RELATIONAL_CONFIG_ID_GENERATOR_TYPES = RELATIONAL_CONFIG + "id-generator-types";

    private static final String SQL_BUILD_CONFIG = "mybatis.no-sugar.sql-build.";
    private static final String SQL_BUILD_CONFIG_SQL_USE_ALIAS = SQL_BUILD_CONFIG + "sql-use-alias";
    private static final String SQL_BUILD_CONFIG_IGNORE_RESULT_LOGIC_DELETE = SQL_BUILD_CONFIG + "ignore-result-logic-delete";
    private static final String SQL_BUILD_CONFIG_IGNORE_EMPTY_CHAR = SQL_BUILD_CONFIG + "ignore-empty-char";
    private static final String SQL_BUILD_CONFIG_DIALECT_CLASS = SQL_BUILD_CONFIG + "dialect-class";

    private final ConfigRegistry configRegistry;
    private final Properties properties;

    public MapperBuilderConfigBuilder(ConfigRegistry configRegistry, Properties properties) {
        this.configRegistry = configRegistry;
        this.properties = properties;
    }

    public MapperBuilderConfig build() {
        MapperBuilderConfig config = setSingletonBean(MapperBuilderConfig.class, this::buildByProperties, null);
        setSingletonBean(SwitchConfig.class, config::getSwitchConfig, config::setSwitchConfig);
        setSingletonBean(RelationalConfig.class, config::getRelationalConfig, config::setRelationalConfig);
        setSingletonBean(SqlBuildConfig.class, config::getSqlBuildConfig, config::setSqlBuildConfig);
        return config;
    }

    private MapperBuilderConfig buildByProperties() {
        MapperBuilderConfig config = new MapperBuilderConfig();
        if (properties == null) {
            return config;
        }
        SwitchConfig switchConfig = config.getSwitchConfig();
        RelationalConfig relationalConfig = config.getRelationalConfig();
        SqlBuildConfig sqlBuildConfig = config.getSqlBuildConfig();

        setPrimitiveValue(SWITCH_CONFIG_CRUD, Boolean::parseBoolean, () -> true, switchConfig::setCrud);
        setPrimitiveValue(SWITCH_CONFIG_FIND_BY_METHODNAME, Boolean::parseBoolean, () -> true, switchConfig::setFindByMethodName);
        setPrimitiveValue(SWITCH_CONFIG_MUTATIVE, Boolean::parseBoolean, () -> true, switchConfig::setMutative);
        setPrimitiveValue(SWITCH_CONFIG_LOGIC_DELETE, Boolean::parseBoolean, () -> true, switchConfig::setLogicDelete);
        setPrimitiveValue(SWITCH_CONFIG_VERSION, Boolean::parseBoolean, () -> true, switchConfig::setVersion);
        setPrimitiveValue(SWITCH_CONFIG_LAZY_BUILDER, Boolean::parseBoolean, () -> true, switchConfig::setLazyBuilder);
        setPrimitiveValue(SWITCH_CONFIG_SPEED_BATCH, Boolean::parseBoolean, () -> true, switchConfig::setSpeedBatch);

        setClassValue(RELATIONAL_CONFIG_ENTITY_BUILDER_CLASS, AbstractEntityBuilder.class, () -> DefaultEntityBuilder.class, relationalConfig::setEntityBuilderType);
        setPrimitiveValue(RELATIONAL_CONFIG_ACCESS_TYPE, AccessType::valueOf, () -> AccessType.FIELD, relationalConfig::setAccessType);
        setPrimitiveValue(RELATIONAL_CONFIG_CLASS_NAME_TO_TABLE_NAME_STRATEGY, NameStrategyType::valueOf, () -> NameStrategyType.CAMEL_TO_UNDERSCORE, relationalConfig::setClassNameToTableNameStrategy);
        setBeanValue(RELATIONAL_CONFIG_CLASS_NAME_TO_TABLE_NAME_STRATEGY_CLASS, NameStrategy.class, () -> null, relationalConfig::setClassNameToTableNameStrategy);
        setPrimitiveValue(RELATIONAL_CONFIG_FIELD_NAME_TO_COLUMN_NAME_STRATEGY, NameStrategyType::valueOf, () -> NameStrategyType.CAMEL_TO_UNDERSCORE, relationalConfig::setFieldNameToColumnNameStrategy);
        setBeanValue(RELATIONAL_CONFIG_FIELD_NAME_TO_COLUMN_NAME_STRATEGY_CLASS, NameStrategy.class, () -> null, relationalConfig::setFieldNameToColumnNameStrategy);
        setPrimitiveValue(RELATIONAL_CONFIG_JAVAX_VALIDATION_MAPPING_NOT_NULL, Boolean::parseBoolean, () -> true, relationalConfig::setJavaxValidationMappingNotNull);
        setIdGeneratorTypes(relationalConfig::setIdGenerators);

        setPrimitiveValue(SQL_BUILD_CONFIG_SQL_USE_ALIAS, Boolean::parseBoolean, () -> true, sqlBuildConfig::setUseTableAlias);
        setPrimitiveValue(SQL_BUILD_CONFIG_IGNORE_RESULT_LOGIC_DELETE, Boolean::parseBoolean, () -> true, sqlBuildConfig::setIgnoreResultLogicDelete);
        setPrimitiveValue(SQL_BUILD_CONFIG_IGNORE_EMPTY_CHAR, Boolean::parseBoolean, () -> false, sqlBuildConfig::setIgnoreEmptyChar);
        setBeanValue(SQL_BUILD_CONFIG_DIALECT_CLASS, Dialect.class, () -> null, sqlBuildConfig::setDialect);
        return config;
    }

    private <T> void setPrimitiveValue(String key, Function<String, T> of, Supplier<T> defaultSupplier, Consumer<T> action) {
        String valueStr = properties.getProperty(key);
        T value = StringUtils.isNotBlank(valueStr) ? of.apply(valueStr) : defaultSupplier.get();
        action.accept(value);
    }

    @SuppressWarnings("unchecked")
    private <T> void setBeanValue(String key, Class<T> superClass, Supplier<T> defaultSupplier, Consumer<T> action) {
        String value = properties.getProperty(key);
        T bean;
        if (StringUtils.isNotBlank(value)) {
            try {
                Class<?> clazz = Class.forName(value);
                Preconditions.checkArgument(superClass.isAssignableFrom(clazz), true
                        , StringFormatter.format("配置类型[{}]需实现[{}]!", value, superClass));
                bean = (T) ReflectionUtils.newInstance(clazz);
            } catch (Exception e) {
                throw new NoSugarException(e);
            }
        } else {
            bean = defaultSupplier.get();
        }
        if (action != null && bean != null) {
            action.accept(bean);
        }
    }

    private void setClassValue(String key, Class<?> superClass, Supplier<Class<?>> defaultSupplier, Consumer<Class<?>> action) {
        String value = properties.getProperty(key);
        Class<?> clazz;
        if (StringUtils.isNotBlank(value)) {
            try {
                clazz = Class.forName(value);
                Preconditions.checkArgument(superClass.isAssignableFrom(clazz), true
                        , StringFormatter.format("配置类型[{}]需实现[{}]!", value, superClass));
            } catch (Exception e) {
                throw new NoSugarException(e);
            }
        } else {
            clazz = defaultSupplier.get();
        }
        if (action != null && clazz != null) {
            action.accept(clazz);
        }
    }

    @SuppressWarnings("unchecked")
    private void setIdGeneratorTypes(Consumer<Map<String, IdGenerator<?>>> action) {
        Map<String, String> map = getSimpleMapByProperties(properties, RELATIONAL_CONFIG_ID_GENERATOR_TYPES);
        if (!map.isEmpty()) {
            Map<String, IdGenerator<?>> mapValue = new HashMap<>();
            map.forEach((name, className) -> {
                try {
                    Class<?> clazz = Class.forName(className);
                    Preconditions.checkArgument(IdGenerator.class.isAssignableFrom(clazz), true
                            , StringFormatter.format("类型[{}]需实现[{}]!", className, IdGenerator.class));
                    mapValue.put(name, ReflectionUtils.newInstance((Class<IdGenerator<?>>) clazz));
                } catch (ClassNotFoundException e) {
                    throw new NoSugarException(e);
                }
            });
            if (action != null) {
                action.accept(mapValue);
            }
        }
    }

    private <T> T setSingletonBean(Class<T> clazz, Supplier<T> defaultSupplier, Consumer<T> action) {
        T bean = null;
        if (configRegistry != null) {
            bean = configRegistry.getConfig(clazz);
        }
        bean = bean == null ? defaultSupplier.get() : bean;
        if (action != null) {
            action.accept(bean);
        }
        return bean;
    }

    /**
     * 获取map 的简单实现
     *
     * @param properties
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<String, String> getSimpleMapByProperties(Properties properties, String key) {
        Map<String, String> map = (Map<String, String>) properties.get(RELATIONAL_CONFIG_ID_GENERATOR_TYPES);
        if (map == null) {
            map = new HashMap<>();
        }
        if (!map.isEmpty()) {
            return map;
        }
        for (String stringPropertyName : properties.stringPropertyNames()) {
            if (stringPropertyName.startsWith(key)) {
                String substring = stringPropertyName.substring(key.length());
                if (substring.startsWith(".")) {
                    substring = substring.substring(1);
                } else if (substring.startsWith("[") && substring.endsWith("]")) {
                    substring = substring.substring(1, substring.length() - 1);
                } else {
                    substring = null;
                }
                if (substring != null) {
                    map.put(substring, properties.getProperty(stringPropertyName));
                }
            }
        }
        return map;
    }

}
