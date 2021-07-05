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

package com.nosugarice.mybatis.mapping;

import com.nosugarice.mybatis.mapping.value.KeyValue;
import com.nosugarice.mybatis.mapping.value.LogicDeleteValue;
import com.nosugarice.mybatis.mapping.value.Value;
import com.nosugarice.mybatis.mapping.value.VersionValue;
import com.nosugarice.mybatis.util.Preconditions;
import org.apache.ibatis.type.TypeHandler;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/11/22
 */
public class RelationalProperty extends Relational<RelationalProperty> implements Comparable<RelationalProperty> {

    private RelationalEntity relationalEntity;

    private Column column;

    /** 字段属性名称 */
    private String name;

    /** java 类型 */
    private Class<?> javaType;

    /** 可为空 */
    private boolean nullable = true;

    /** 是否主键 */
    private boolean primaryKey;

    /** 乐观锁 */
    private boolean version;

    /** 逻辑删除 */
    private boolean logicDelete;

    /** 值 */
    private Value<? extends Serializable> value;

    /** typeHandler */
    private Class<? extends TypeHandler<?>> typeHandler;

    /** 查询默认排序 */
    private String orderBy;

    /** 忽略空字符 */
    private boolean ignoreEmptyChar;

    @Override
    public int compareTo(RelationalProperty relationalProperty) {
        return column.getOrder() - relationalProperty.getColumn().getOrder();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RelationalProperty)) {
            return false;
        }
        RelationalProperty relationalProperty = (RelationalProperty) obj;
        if (column != relationalProperty.getColumn()) {
            return false;
        }
        return Objects.equals(name, relationalProperty.getName());
    }

    public boolean isChar() {
        return CharSequence.class.isAssignableFrom(javaType);
    }

    public KeyValue<?> getAsKeyValue() {
        Preconditions.checkArgument(isPrimaryKey(), true, name + ":不是主键类型!");
        return (KeyValue<?>) getValue();
    }

    public VersionValue getAsVersionValue() {
        Preconditions.checkArgument(isVersion(), true, name + ":不是版本类型!");
        return (VersionValue) getValue();
    }

    public LogicDeleteValue getAsLogicDeleteValue() {
        Preconditions.checkArgument(isLogicDelete(), true, name + ":不是逻辑删除类型!");
        return (LogicDeleteValue) getValue();
    }

    public RelationalEntity getRelationalModel() {
        return relationalEntity;
    }

    public void setRelationalModel(RelationalEntity relationalEntity) {
        this.relationalEntity = relationalEntity;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isVersion() {
        return version;
    }

    public void setVersion(boolean version) {
        this.version = version;
    }

    public boolean isLogicDelete() {
        return logicDelete;
    }

    public void setLogicDelete(boolean logicDelete) {
        this.logicDelete = logicDelete;
    }

    public Value<? extends Serializable> getValue() {
        return value;
    }

    public void setValue(Value<? extends Serializable> value) {
        this.value = value;
    }

    public Class<? extends TypeHandler<?>> getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(Class<? extends TypeHandler<?>> typeHandler) {
        this.typeHandler = typeHandler;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public boolean isIgnoreEmptyChar() {
        return ignoreEmptyChar;
    }

    public void setIgnoreEmptyChar(boolean ignoreEmptyChar) {
        this.ignoreEmptyChar = ignoreEmptyChar;
    }
}
