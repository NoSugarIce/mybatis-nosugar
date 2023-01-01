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

package com.nosugarice.mybatis.support;

import com.nosugarice.mybatis.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 动态表名映射器,实体配置动态表名(@DynamicTableName),每次执行数据库操作会动态的来获取对应的运行时表名
 * 线程关联的TABLE_NAME_MAPPING 优先级高于 表名转换器TABLE_NAME_HANDLER_REGISTRY
 *
 * @author dingjingyang@foxmail.com
 * @date 2021/1/17
 */
public class DynamicTableNameMapping {

    private static final ThreadLocal<Map<String, String>> TABLE_NAME_MAPPING = new ThreadLocal<>();
    private static final Map<String, Function<String, String>> TABLE_NAME_HANDLER_REGISTRY = new HashMap<>();

    public static String getName(String tableName) {
        Map<String, String> tableNameMapping = TABLE_NAME_MAPPING.get();
        if (tableNameMapping != null && tableNameMapping.containsKey(tableName)) {
            String runTimeTableName = tableNameMapping.get(tableName);
            if (StringUtils.isNotBlank(runTimeTableName)) {
                return runTimeTableName;
            }
        }
        Function<String, String> function = TABLE_NAME_HANDLER_REGISTRY.get(tableName);
        if (function != null) {
            String runTimeTableName = function.apply(tableName);
            if (StringUtils.isNotBlank(runTimeTableName)) {
                return runTimeTableName;
            }
        }
        return tableName;
    }

    public static void setTableName(String tableName, String runTimeTableName) {
        Map<String, String> tableNameMapping = TABLE_NAME_MAPPING.get();
        if (tableNameMapping == null) {
            TABLE_NAME_MAPPING.set(new HashMap<>());
        }
        tableNameMapping = TABLE_NAME_MAPPING.get();
        tableNameMapping.put(tableName, runTimeTableName);
    }

    public static void setMapping(Map<String, String> tableNameMapping) {
        TABLE_NAME_MAPPING.set(tableNameMapping);
    }

    public static void registerHandler(String tableName, Function<String, String> handler) {
        TABLE_NAME_HANDLER_REGISTRY.put(tableName, handler);
    }

    public static void clean() {
        TABLE_NAME_MAPPING.remove();
    }

}
