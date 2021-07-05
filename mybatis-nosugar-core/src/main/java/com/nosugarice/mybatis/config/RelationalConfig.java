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
import com.nosugarice.mybatis.support.NameStrategy;
import com.nosugarice.mybatis.support.NameStrategyType;
import com.nosugarice.mybatis.valuegenerator.id.IdGenerator;

import javax.persistence.AccessType;
import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/6
 */
public class RelationalConfig {

    private Class<? extends AbstractEntityBuilder<?>> entityBuilderType;

    /** 属性从哪种类型获取 */
    private AccessType accessType = AccessType.FIELD;

    /** 类名转换到表名的方式 */
    private NameStrategy classNameToTableNameStrategy = NameStrategyType.CAMEL_TO_UNDERSCORE;

    /** 属性名转换到表列名的方式 */
    private NameStrategy fieldNameToColumnNameStrategy = NameStrategyType.CAMEL_TO_UNDERSCORE;

    /** javax.validation 的注解映射到数据库的非空判断 */
    private boolean javaxValidationMappingNotNull = true;

    private Map<String, IdGenerator<?>> idGenerators;

    public Class<? extends AbstractEntityBuilder<?>> getEntityBuilderType() {
        return entityBuilderType;
    }

    @SuppressWarnings("unchecked")
    public void setEntityBuilderType(Class<?> entityBuilderType) {
        this.entityBuilderType = (Class<? extends AbstractEntityBuilder<?>>) entityBuilderType;
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

    public boolean isJavaxValidationMappingNotNull() {
        return javaxValidationMappingNotNull;
    }

    public void setJavaxValidationMappingNotNull(boolean javaxValidationMappingNotNull) {
        this.javaxValidationMappingNotNull = javaxValidationMappingNotNull;
    }

    public Map<String, IdGenerator<?>> getIdGenerators() {
        return idGenerators;
    }

    public void setIdGenerators(Map<String, IdGenerator<?>> idGenerators) {
        this.idGenerators = idGenerators;
    }
}
