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
import com.nosugarice.mybatis.handler.ValueHandler;
import com.nosugarice.mybatis.support.NameStrategy;

import javax.persistence.AccessType;
import java.util.List;
import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/6
 */
public class RelationalConfig {

    private MapperStrategy mapperStrategy;

    private Class<? extends EntityBuilder> entityBuilderType;

    /** 属性从哪种类型获取 */
    private AccessType accessType = AccessType.FIELD;

    /** 类名转换到表名的方式 */
    private NameStrategy classNameToTableNameStrategy;

    /** 属性名转换到表列名的方式 */
    private NameStrategy fieldNameToColumnNameStrategy;

    private Map<String, IdGenerator<?>> idGenerators;

    private List<ValueHandler<?>> valueHandlers;

    public MapperStrategy getMapperStrategy() {
        return mapperStrategy;
    }

    public void setMapperStrategy(MapperStrategy mapperStrategy) {
        this.mapperStrategy = mapperStrategy;
    }

    public Class<? extends EntityBuilder> getEntityBuilderType() {
        return entityBuilderType;
    }

    @SuppressWarnings("unchecked")
    public void setEntityBuilderType(Class<?> entityBuilderType) {
        this.entityBuilderType = (Class<? extends EntityBuilder>) entityBuilderType;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public NameStrategy getClassNameToTableNameStrategy() {
        return classNameToTableNameStrategy;
    }

    public void setClassNameToTableNameStrategy(NameStrategy classNameToTableNameStrategy) {
        this.classNameToTableNameStrategy = classNameToTableNameStrategy;
    }

    public NameStrategy getFieldNameToColumnNameStrategy() {
        return fieldNameToColumnNameStrategy;
    }

    public void setFieldNameToColumnNameStrategy(NameStrategy fieldNameToColumnNameStrategy) {
        this.fieldNameToColumnNameStrategy = fieldNameToColumnNameStrategy;
    }

    public Map<String, IdGenerator<?>> getIdGenerators() {
        return idGenerators;
    }

    public void setIdGenerators(Map<String, IdGenerator<?>> idGenerators) {
        this.idGenerators = idGenerators;
    }

    public List<ValueHandler<?>> getValueHandlers() {
        return valueHandlers;
    }

    @SuppressWarnings("unchecked")
    public void setValueHandlers(List<?> valueHandlers) {
        this.valueHandlers = (List<ValueHandler<?>>) valueHandlers;
    }

}
