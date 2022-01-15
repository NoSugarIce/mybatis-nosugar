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

import com.nosugarice.mybatis.builder.mapper.AbstractMapperBuilder;
import com.nosugarice.mybatis.builder.mapper.AdapterMapperBuilder;
import com.nosugarice.mybatis.builder.mapper.CrudMapperBuilder;
import com.nosugarice.mybatis.builder.mapper.JpaMapperBuilder;
import com.nosugarice.mybatis.config.internal.DefaultEntityBuilder;
import com.nosugarice.mybatis.config.internal.DefaultMapperStrategy;
import com.nosugarice.mybatis.config.internal.NameStrategyType;
import com.nosugarice.mybatis.dialect.Dialect;
import com.nosugarice.mybatis.exception.NoSugarException;
import com.nosugarice.mybatis.handler.ValueHandler;
import com.nosugarice.mybatis.mapping.id.IdGenerator;
import com.nosugarice.mybatis.registry.ConfigRegistry;
import com.nosugarice.mybatis.support.EntityPropertyNameStrategy;
import com.nosugarice.mybatis.support.NameStrategy;
import com.nosugarice.mybatis.util.CollectionUtils;
import com.nosugarice.mybatis.util.Preconditions;
import com.nosugarice.mybatis.util.ReflectionUtils;
import com.nosugarice.mybatis.util.StringFormatter;
import com.nosugarice.mybatis.util.StringUtils;

import javax.persistence.AccessType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    private static final String SWITCH_CONFIG_INCLUDE_MAPPER_BUILDERS = SWITCH_CONFIG + "include-mapper-builders";
    private static final String SWITCH_CONFIG_EXCLUDE_MAPPER_BUILDERS = SWITCH_CONFIG + "exclude-mapper-builders";
    private static final String SWITCH_CONFIG_LOGIC_DELETE = SWITCH_CONFIG + "logic-delete";
    private static final String SWITCH_CONFIG_VERSION = SWITCH_CONFIG + "version";
    private static final String SWITCH_CONFIG_LAZY_BUILDER = SWITCH_CONFIG + "lazy-builder";
    private static final String SWITCH_CONFIG_SPEED_BATCH = SWITCH_CONFIG + "speed-batch";

    private static final String RELATIONAL_CONFIG = "mybatis.no-sugar.relational.";
    private static final String RELATIONAL_CONFIG_MAPPER_STRATEGY_CLASS = RELATIONAL_CONFIG + "mapper-strategy";
    private static final String RELATIONAL_CONFIG_ENTITY_BUILDER_CLASS = RELATIONAL_CONFIG + "entity-builder-class";
    private static final String RELATIONAL_CONFIG_ACCESS_TYPE = RELATIONAL_CONFIG + "access-type";
    private static final String RELATIONAL_CONFIG_CLASS_NAME_TO_TABLE_NAME_STRATEGY = RELATIONAL_CONFIG + "class-name-to-table-name-strategy";
    private static final String RELATIONAL_CONFIG_CLASS_NAME_TO_TABLE_NAME_STRATEGY_CLASS = RELATIONAL_CONFIG + "class-name-to-table-name-strategy-class";
    private static final String RELATIONAL_CONFIG_FIELD_NAME_TO_COLUMN_NAME_STRATEGY = RELATIONAL_CONFIG + "field-name-to-column-name-strategy";
    private static final String RELATIONAL_CONFIG_FIELD_NAME_TO_COLUMN_NAME_STRATEGY_CLASS = RELATIONAL_CONFIG + "field-name-to-column-name-strategy-class";
    private static final String RELATIONAL_CONFIG_ID_GENERATOR_TYPES = RELATIONAL_CONFIG + "id-generator-types";
    private static final String RELATIONAL_CONFIG_VALUE_HANDLER_TYPES = RELATIONAL_CONFIG + "value-handler-types";

    private static final String SQL_BUILD_CONFIG = "mybatis.no-sugar.sql-build.";
    private static final String SQL_BUILD_CONFIG_IGNORE_EMPTY_CHAR = SQL_BUILD_CONFIG + "ignore-empty-char";
    private static final String SQL_BUILD_CONFIG_DIALECT_CLASS = SQL_BUILD_CONFIG + "dialect-class";

    private final ConfigRegistry configRegistry;
    private Properties properties;

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
            properties = new Properties();
        }
        SwitchConfig switchConfig = config.getSwitchConfig();
        RelationalConfig relationalConfig = config.getRelationalConfig();
        SqlBuildConfig sqlBuildConfig = config.getSqlBuildConfig();

        setClassesValue(SWITCH_CONFIG_INCLUDE_MAPPER_BUILDERS
                , AbstractMapperBuilder.class, () -> Arrays.asList(
                        CrudMapperBuilder.class
                        , JpaMapperBuilder.class
                        , AdapterMapperBuilder.class)
                , classes -> classes.forEach(switchConfig::includeMapperBuilder));
        setClassesValue(SWITCH_CONFIG_EXCLUDE_MAPPER_BUILDERS, AbstractMapperBuilder.class, () -> null
                , classes -> classes.forEach(switchConfig::excludeMapperBuilder));
        setPrimitiveValue(SWITCH_CONFIG_LOGIC_DELETE, Boolean::parseBoolean, () -> true, switchConfig::setLogicDelete);
        setPrimitiveValue(SWITCH_CONFIG_VERSION, Boolean::parseBoolean, () -> true, switchConfig::setVersion);
        setPrimitiveValue(SWITCH_CONFIG_LAZY_BUILDER, Boolean::parseBoolean, () -> true, switchConfig::setLazyBuilder);
        setPrimitiveValue(SWITCH_CONFIG_SPEED_BATCH, Boolean::parseBoolean, () -> true, switchConfig::setSpeedBatch);

        setBeanValue(RELATIONAL_CONFIG_MAPPER_STRATEGY_CLASS, MapperStrategy.class, DefaultMapperStrategy::new, relationalConfig::setMapperStrategy);
        setClassValue(RELATIONAL_CONFIG_ENTITY_BUILDER_CLASS, EntityBuilder.class, () -> DefaultEntityBuilder.class, relationalConfig::setEntityBuilderType);
        setPrimitiveValue(RELATIONAL_CONFIG_ACCESS_TYPE, AccessType::valueOf, () -> AccessType.FIELD, relationalConfig::setAccessType);
        setPrimitiveValue(RELATIONAL_CONFIG_CLASS_NAME_TO_TABLE_NAME_STRATEGY, NameStrategyType::valueOf, () -> NameStrategyType.CAMEL_TO_UNDERSCORE, relationalConfig::setClassNameToTableNameStrategy);
        setBeanValue(RELATIONAL_CONFIG_CLASS_NAME_TO_TABLE_NAME_STRATEGY_CLASS, NameStrategy.class, () -> null, relationalConfig::setClassNameToTableNameStrategy);
        setPrimitiveValue(RELATIONAL_CONFIG_FIELD_NAME_TO_COLUMN_NAME_STRATEGY, NameStrategyType::valueOf, () -> NameStrategyType.CAMEL_TO_UNDERSCORE, relationalConfig::setFieldNameToColumnNameStrategy);
        setBeanValue(RELATIONAL_CONFIG_FIELD_NAME_TO_COLUMN_NAME_STRATEGY_CLASS, EntityPropertyNameStrategy.class, () -> null, relationalConfig::setFieldNameToColumnNameStrategy);
        setIdGeneratorTypes(relationalConfig::setIdGenerators);
        setClassInstanceValue(RELATIONAL_CONFIG_VALUE_HANDLER_TYPES, ValueHandler.class, relationalConfig::setValueHandlers);

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
                Preconditions.checkArgument(superClass.isAssignableFrom(clazz)
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
                Preconditions.checkArgument(superClass.isAssignableFrom(clazz)
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

    private void setClassesValue(String key, Class<?> superClass, Supplier<List<Class<?>>> defaultSupplier, Consumer<List<Class<?>>> action) {
        String value = properties.getProperty(key);
        List<Class<?>> clazzs = null;
        if (StringUtils.isNotBlank(value)) {
            try {
                value = value.replace(",", ";");
                String[] classNames = value.split(";");
                clazzs = new ArrayList<>(classNames.length);
                for (String className : classNames) {
                    Class<?> clazz = Class.forName(className);
                    Preconditions.checkArgument(superClass.isAssignableFrom(clazz)
                            , StringFormatter.format("配置类型[{}]需实现[{}]!", value, superClass));
                    clazzs.add(clazz);
                }
            } catch (Exception e) {
                throw new NoSugarException(e);
            }
        }
        if (CollectionUtils.isEmpty(clazzs)) {
            clazzs = defaultSupplier.get();
        }
        if (action != null && clazzs != null) {
            action.accept(clazzs);
        }
    }

    private void setClassInstanceValue(String key, Class<?> superClass, Consumer<List<?>> action) {
        String value = properties.getProperty(key);
        List<Object> instances = null;
        if (StringUtils.isNotBlank(value)) {
            try {
                value = value.replace(",", ";");
                String[] classNames = value.split(";");
                instances = new ArrayList<>(classNames.length);
                for (String className : classNames) {
                    Class<?> clazz = Class.forName(className);
                    Preconditions.checkArgument(superClass.isAssignableFrom(clazz)
                            , StringFormatter.format("配置类型[{}]需实现[{}]!", value, superClass));
                    instances.add(ReflectionUtils.newInstance(clazz));
                }
            } catch (Exception e) {
                throw new NoSugarException(e);
            }
        }
        if (action != null && instances != null) {
            action.accept(instances);
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
                    Preconditions.checkArgument(IdGenerator.class.isAssignableFrom(clazz)
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
        Map<String, String> map = (Map<String, String>) properties.get(key);
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
