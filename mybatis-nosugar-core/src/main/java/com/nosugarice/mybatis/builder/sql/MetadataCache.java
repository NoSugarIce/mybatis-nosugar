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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dingjingyang@foxmail.com(dingjingyang)
 * @date 2021/3/21
 */
public class MetadataCache {

    private static final Map<Class<?>, PropertySqlIndex> ENTITY_CACHE = new HashMap<>();

    public static void putPropertyCache(Class<?> entityClass, Collection<PropertySqlPart> propertySqlParts) {
        ENTITY_CACHE.put(entityClass, new PropertySqlIndex(propertySqlParts));
    }

    public static PropertyCache getByProperty(Class<?> entityClass, String property) {
        return Optional.ofNullable(ENTITY_CACHE.get(entityClass))
                .map(propertySqlIndex -> propertySqlIndex.getByProperty(property)).orElse(null);
    }

    public static String getColumnByProperty(Class<?> entityClass, String property) {
        return Optional.ofNullable(ENTITY_CACHE.get(entityClass))
                .map(propertySqlIndex -> propertySqlIndex.getByProperty(property))
                .map(PropertyCache::getColumn)
                .orElseThrow(() -> new NullPointerException("未找到[" + property + "]属性对应的列信息!"));
    }

    public static List<PropertyCache> getPropertyCaches(Class<?> entityClass) {
        return Optional.ofNullable(ENTITY_CACHE.get(entityClass))
                .map(PropertySqlIndex::getPropertyCaches)
                .orElse(Collections.emptyList());
    }

    private static class PropertySqlIndex {

        private final List<PropertyCache> propertyCaches;
        private final Map<String, PropertyCache> columnCacheMap;
        private final Map<String, PropertyCache> propertyCacheMap;

        private PropertySqlIndex(Collection<PropertySqlPart> propertySqlParts) {
            this.propertyCaches = propertySqlParts.stream().map(PropertyCache::new).collect(Collectors.toList());
            this.columnCacheMap = propertyCaches.stream()
                    .collect(Collectors.toMap(PropertyCache::getColumn, Function.identity(), (v, v1) -> v
                            , () -> new HashMap<>(propertyCaches.size(), 1)));
            this.propertyCacheMap = propertyCaches.stream()
                    .collect(Collectors.toMap(PropertyCache::getProperty, Function.identity(), (v, v1) -> v
                            , () -> new HashMap<>(propertyCaches.size(), 1)));
        }

        private PropertyCache getByColumn(String column) {
            return columnCacheMap.get(column);
        }

        private PropertyCache getByProperty(String property) {
            return propertyCacheMap.get(property);
        }

        public List<PropertyCache> getPropertyCaches() {
            return propertyCaches;
        }
    }

    public static class PropertyCache {

        private final boolean primaryKey;
        private final String column;
        private final String safeColumn;
        private final String property;
        private final String resultItem;
        private final String assignJdbcType;
        private final String assignTypeHandler;

        public PropertyCache(PropertySqlPart propertySqlPart) {
            this.primaryKey = propertySqlPart.property.isPrimaryKey();
            this.column = propertySqlPart.column;
            this.safeColumn = propertySqlPart.safeColumn;
            this.property = propertySqlPart.propertyName;
            this.resultItem = propertySqlPart.safeColumn + SqlPart.SPACE + SqlPart.AS + "\"" + property + "\"";
            ;
            this.assignJdbcType = propertySqlPart.assignJdbcType;
            this.assignTypeHandler = propertySqlPart.assignTypeHandler;
        }

        public boolean isPrimaryKey() {
            return primaryKey;
        }

        public String getColumn() {
            return column;
        }

        public String getSafeColumn() {
            return safeColumn;
        }

        public String getProperty() {
            return property;
        }

        public String getResultItem() {
            return resultItem;
        }

        public String getAssignJdbcType() {
            return assignJdbcType;
        }

        public String getAssignTypeHandler() {
            return assignTypeHandler;
        }
    }

}
