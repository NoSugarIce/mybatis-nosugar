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

package com.nosugarice.mybatis.mapping;

import com.nosugarice.mybatis.assign.value.KeyValue;
import com.nosugarice.mybatis.assign.value.LogicDeleteValue;
import com.nosugarice.mybatis.assign.value.Value;
import com.nosugarice.mybatis.assign.value.VersionValue;
import com.nosugarice.mybatis.util.Preconditions;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/11/22
 */
public class RelationalProperty {

    private final Member member;

    /** 字段属性名称 */
    private String name;

    /** java 类型 */
    private Class<?> javaType;

    /** 对应列名 */
    private String column;

    /** 长度 */
    private int length;

    /** 精度 */
    private int precision;

    /** 保留小数点位数 */
    private int scale;

    /** jdbc类型 */
    private Integer jdbcType;

    /** jdbc类型名称 */
    private String jdbcTypeName;

    /** 是否是关键字 */
    private boolean sqlKeyword;

    /** 可为空 */
    private boolean nullable = true;

    /** 是否主键 */
    private boolean primaryKey;

    /** 自增 */
    private boolean autoIncrement;

    /** 乐观锁 */
    private boolean version;

    /** 逻辑删除 */
    private boolean logicDelete;

    /** 值 */
    private Value value;

    /** typeHandler */
    private Class<? extends TypeHandler<?>> typeHandler;

    /** 查询默认排序 */
    private String orderBy;

    public RelationalProperty(Member member) {
        ((AccessibleObject) member).setAccessible(true);
        this.member = member;
    }

    public Object valueByObj(Object obj) {
        try {
            return member instanceof Field ? ((Field) member).get(obj) : ((Method) member).invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public KeyValue getAsKeyValue() {
        Preconditions.checkArgument(isPrimaryKey(), name + ":不是主键类型!");
        return (KeyValue) getValue();
    }

    public VersionValue getAsVersionValue() {
        Preconditions.checkArgument(isVersion(), name + ":不是版本类型!");
        return (VersionValue) getValue();
    }

    public LogicDeleteValue getAsLogicDeleteValue() {
        Preconditions.checkArgument(isLogicDelete(), name + ":不是逻辑删除类型!");
        return (LogicDeleteValue) getValue();
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

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public Integer getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(Integer jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getJdbcTypeName() {
        return jdbcTypeName;
    }

    public void setJdbcTypeName(String jdbcTypeName) {
        this.jdbcTypeName = jdbcTypeName;
    }

    public boolean isSqlKeyword() {
        return sqlKeyword;
    }

    public void setSqlKeyword(boolean sqlKeyword) {
        this.sqlKeyword = sqlKeyword;
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

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
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

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
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

}
