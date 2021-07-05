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

import com.nosugarice.mybatis.annotation.DynamicTableName;
import com.nosugarice.mybatis.annotation.SpeedBatch;
import com.nosugarice.mybatis.annotation.SupportedFunction;
import com.nosugarice.mybatis.mapping.RelationalEntity;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.mapping.value.KeyValue;
import com.nosugarice.mybatis.util.Preconditions;

import java.lang.reflect.Method;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/1/7
 */
public class Supports {

    private final RelationalEntity relationalEntity;

    private final boolean supportSqlUseAlias;
    private final boolean supportIgnoreResultLogicDelete;
    private final boolean supportIgnoreEmptyChar;

    private final boolean supportLogicDelete;
    private final boolean supportVersion;

    private final boolean supportPrimaryKey;
    private final boolean supportAutoIncrement;
    private final boolean supportIdGenerator;
    private final boolean supportDynamicTableName;

    public Supports(RelationalEntity relationalEntity, MapperBuilderConfig config) {
        this.relationalEntity = relationalEntity;

        this.supportSqlUseAlias = supportSqlUseAlias(config.getSqlBuildConfig());
        this.supportIgnoreResultLogicDelete = supportIgnoreResultLogicDelete(config.getSqlBuildConfig());
        this.supportIgnoreEmptyChar = supportIgnoreEmptyChar(config.getSqlBuildConfig());

        this.supportLogicDelete = supportLogicDelete(relationalEntity, config.getSwitchConfig());
        this.supportVersion = supportVersion(relationalEntity, config.getSwitchConfig());

        this.supportPrimaryKey = supportPrimaryKey(relationalEntity);
        this.supportAutoIncrement = supportAutoIncrement(relationalEntity);
        this.supportIdGenerator = supportIdGenerator(relationalEntity);
        this.supportDynamicTableName = supportDynamicTableName(relationalEntity);
    }

    private boolean supportSqlUseAlias(SqlBuildConfig sqlBuildConfig) {
        return sqlBuildConfig.isUseTableAlias();
    }

    private boolean supportIgnoreResultLogicDelete(SqlBuildConfig sqlBuildConfig) {
        return sqlBuildConfig.isIgnoreResultLogicDelete();
    }

    private boolean supportIgnoreEmptyChar(SqlBuildConfig sqlBuildConfig) {
        return sqlBuildConfig.isIgnoreEmptyChar();
    }

    private boolean supportLogicDelete(RelationalEntity relationalEntity, SwitchConfig config) {
        return config.isLogicDelete() && relationalEntity.getLogicDeleteProperty().isPresent();
    }

    private boolean supportVersion(RelationalEntity relationalEntity, SwitchConfig config) {
        return config.isVersion() && relationalEntity.getVersionProperty().isPresent();
    }

    private boolean supportLazyBuilder(SwitchConfig switchConfig) {
        return switchConfig.isLazyBuilder();
    }

    private boolean supportSpeedBatch(Class<?> mapperInterface, SwitchConfig config) {
        return config.isSpeedBatch() && mapperInterface.isAnnotationPresent(SpeedBatch.class);
    }

    private boolean supportPrimaryKey(RelationalEntity relationalEntity) {
        return relationalEntity.getPrimaryKeyProperties().size() == 1;
    }

    private boolean supportAutoIncrement(RelationalEntity relationalEntity) {
        return relationalEntity.getIdGeneratorProperty()
                .map(RelationalProperty::getAsKeyValue)
                .map(KeyValue::isAutoIncrement)
                .orElse(false);
    }

    private boolean supportIdGenerator(RelationalEntity relationalEntity) {
        return relationalEntity.getIdGeneratorProperty()
                .map(RelationalProperty::getAsKeyValue)
                .map(KeyValue::isAutoIncrement)
                .map(isAutoIncrement -> !isAutoIncrement)
                .orElse(false);
    }

    private boolean supportDynamicTableName(RelationalEntity relationalEntity) {
        return relationalEntity.getEntityClass().isAnnotationPresent(DynamicTableName.class);
    }

    public void checkMethod(Method method) {
        SupportedFunction supportedFunctionAnn = method.getAnnotation(SupportedFunction.class);
        if (supportedFunctionAnn != null) {
            if (!this.supportPrimaryKey) {
                boolean annSupportPrimaryKey = supportedFunctionAnn.supportPrimaryKey();
                Preconditions.checkArgument(!annSupportPrimaryKey, true, "[" + relationalEntity.getQualifiedName() + "]"
                        + "方法[" + method.getName() + "] 请设置唯一主键或Mapper不要继承[PrimaryKeyMapper].");
            }
            if (!this.supportLogicDelete) {
                boolean annSupportLogicDelete = supportedFunctionAnn.supportLogicDelete();
                Preconditions.checkArgument(!annSupportLogicDelete, false, "[" + relationalEntity.getQualifiedName() + "]"
                        + "方法[" + method.getName() + "] 请设置逻辑删除字段或Mapper不要继承[LogicDeleteMapper]" +
                        ". 否则相关方法时将不生效");
            }
        }
    }

    public boolean isSupportSqlUseAlias() {
        return supportSqlUseAlias;
    }

    public boolean isSupportIgnoreResultLogicDelete() {
        return supportIgnoreResultLogicDelete;
    }

    public boolean isSupportIgnoreEmptyChar() {
        return supportIgnoreEmptyChar;
    }

    public boolean isSupportLogicDelete() {
        return supportLogicDelete;
    }

    public boolean isSupportVersion() {
        return supportVersion;
    }

    public boolean isSupportPrimaryKey() {
        return supportPrimaryKey;
    }

    public boolean isSupportAutoIncrement() {
        return supportAutoIncrement;
    }

    public boolean isSupportIdGenerator() {
        return supportIdGenerator;
    }

    public boolean isSupportDynamicTableName() {
        return supportDynamicTableName;
    }
}
