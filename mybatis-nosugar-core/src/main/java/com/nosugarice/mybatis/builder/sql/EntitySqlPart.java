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

import com.nosugarice.mybatis.config.Supports;
import com.nosugarice.mybatis.dialect.Dialect;
import com.nosugarice.mybatis.mapper.update.UpdateMapper;
import com.nosugarice.mybatis.mapping.RelationalEntity;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.mapping.value.LogicDeleteValue;
import com.nosugarice.mybatis.mapping.value.Value;
import com.nosugarice.mybatis.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/1/1
 */
public class EntitySqlPart implements SqlPart {

    private final RelationalEntity entity;
    private final Supports supports;
    private final Dialect dialect;

    public final List<RelationalProperty> properties;
    public final Map<String, PropertySqlPart> columnSqlParts;

    public String selectResult;
    public String selectParameter;
    public String selectPrimaryKeyColumn;
    public String updatePrimaryKeyColumn;
    public String selectParameterLogicDelete;
    public String updateParameterLogicDelete;
    public String selectParameterVersion;
    public String selectParameterVersionNullable;
    public String updateColumnValue;
    public String updateColumnValueChoseKey;
    public String updateColumnValueNullable;
    public String logicDeleteColumnValue;
    public String updateHead;
    public String deleteHead;

    public EntitySqlPart(RelationalEntity entity, Supports supports, Dialect dialect, boolean creatPublicSql) {
        this.entity = entity;
        this.supports = supports;
        this.dialect = dialect;
        this.properties = new ArrayList<>(entity.getProperties());
        this.columnSqlParts = properties.stream()
                .map(relationalProperty -> new PropertySqlPart(relationalProperty, this.dialect))
                .collect(Collectors.toMap(PropertySqlPart::getPropertyName, Function.identity()));
        MetadataCache.putPropertyCache(entity.getEntityClass().getClassType(), this.columnSqlParts.values());
        if (!creatPublicSql) {
            return;
        }
        this.selectResult = selectResult();
        this.selectParameter = selectParameter();
        this.selectPrimaryKeyColumn = selectPrimaryKeyColumn(null);
        this.updatePrimaryKeyColumn = selectPrimaryKeyColumn(UpdateMapper.UPDATE_COLUMN);
        this.selectParameterLogicDelete = selectParameterLogicDelete();
        this.updateParameterLogicDelete = selectParameterLogicDelete();
        this.selectParameterVersion = selectParameterVersion(false, UpdateMapper.UPDATE_COLUMN);
        this.selectParameterVersionNullable = selectParameterVersion(true, UpdateMapper.UPDATE_COLUMN);
        this.updateColumnValue = updateColumnValue(false, UpdateMapper.UPDATE_COLUMN);
        this.updateColumnValueChoseKey = updateColumnValueChoseKey(UpdateMapper.UPDATE_COLUMN);
        this.updateColumnValueNullable = updateColumnValue(true, UpdateMapper.UPDATE_COLUMN);
        this.logicDeleteColumnValue = logicDeleteColumnValue();
        this.updateHead = UPDATE + Placeholder.TABLE_NAME + LINE_SEPARATOR + SET + LINE_SEPARATOR;
        this.deleteHead = DELETE + LINE_SEPARATOR + Placeholder.FROM_TABLE;
    }

    public String selectResult() {
        StringBuilder sqlBuilder = new StringBuilder();
        for (RelationalProperty property : properties) {
            if (supports.isSupportIgnoreResultLogicDelete() && property.isLogicDelete()) {
                continue;
            }
            PropertySqlPart propertySqlPart = columnSqlParts.get(property.getName());
            sqlBuilder.append(propertySqlPart.resultItem).append(",").append(LINE_SEPARATOR);
        }
        return sqlBuilder.toString();
    }

    public String selectParameter() {
        StringBuilder sqlBuilder = new StringBuilder();
        for (RelationalProperty property : properties) {
            PropertySqlPart propertySqlPart = columnSqlParts.get(property.getName());
            String valueItem = propertySqlPart.valueItem("criteria.entity", AND);
            String tagNotNull = SqlPart.tagNotNull("criteria.entity." + property.getName()
                    , property.isIgnoreEmptyChar(), valueItem, true);
            sqlBuilder.append(tagNotNull);
        }
        return SqlPart.tagNotNull("criteria.entity", false, sqlBuilder.toString());
    }

    public String selectPrimaryKeyColumn(String prefix) {
        StringBuilder sqlBuilder = new StringBuilder();
        List<RelationalProperty> pkProperties = entity.getPrimaryKeyProperties();
        if (pkProperties.isEmpty()) {
            pkProperties = properties;
        }

        for (RelationalProperty property : pkProperties) {
            PropertySqlPart propertySqlPart = columnSqlParts.get(property.getName());
            String valueItem = propertySqlPart.valueItem(prefix, AND);
            sqlBuilder.append(valueItem).append(LINE_SEPARATOR);
        }
        return sqlBuilder.toString();
    }

    public String selectParameterLogicDelete() {
        if (!supports.isSupportLogicDelete()) {
            return EMPTY;
        }
        StringBuilder sqlBuilder = new StringBuilder();
        RelationalProperty logicDeleteProperty = entity.getLogicDeleteProperty().orElseThrow(NullPointerException::new);
        PropertySqlPart propertySqlPart = columnSqlParts.get(logicDeleteProperty.getName());
        sqlBuilder.append(AND);
        sqlBuilder.append(Placeholder.ALIAS_STATE);
        sqlBuilder.append(propertySqlPart.safeColumn);
        Serializable defaultValue = logicDeleteProperty.getValue().getDefaultValue();
        if (defaultValue == null) {
            sqlBuilder.append(" IS NULL ");
        } else {
            String defaultValueStr = dialect.getLiteralValueHandler().convert(defaultValue);
            sqlBuilder.append(EQUALS).append(defaultValueStr);
        }
        sqlBuilder.append(LINE_SEPARATOR);
        return sqlBuilder.toString();
    }

    public String selectParameterVersion(boolean nullable, String prefix) {
        if (!supports.isSupportVersion()) {
            return EMPTY;
        }
        RelationalProperty property = entity.getVersionProperty().orElseThrow(NullPointerException::new);
        PropertySqlPart propertySqlPart = columnSqlParts.get(property.getName());
        String valueItem = propertySqlPart.valueItem(prefix, AND);
        return nullable ? SqlPart.tagNotNull((StringUtils.isNotBlank(prefix) ? prefix + DOT : EMPTY) + property.getName()
                , property.isIgnoreEmptyChar(), valueItem) : valueItem + LINE_SEPARATOR;
    }

    public String insertColumns(boolean nullable) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("INSERT INTO ").append(Placeholder.TABLE_NAME).append(LINE_SEPARATOR);
        StringBuilder insertColumns = new StringBuilder();
        for (RelationalProperty property : properties) {
            if (!property.getValue().isInsertable()) {
                continue;
            }
            PropertySqlPart propertySqlPart = columnSqlParts.get(property.getName());
            String mid = propertySqlPart.safeColumn + ",";
            if (nullable && property.isNullable()) {
                insertColumns.append(SqlPart.tagNotNull(property.getName(), property.isIgnoreEmptyChar(), mid));
            } else {
                insertColumns.append(mid).append(LINE_SEPARATOR);
            }
        }
        String trim = SqlPart.tagTrim(insertColumns.toString(), "(", null, ")", ",");
        sqlBuilder.append(trim);
        sqlBuilder.append("values").append(LINE_SEPARATOR);
        return sqlBuilder.toString();
    }

    public String insertValues(boolean nullable) {
        StringBuilder insertValues = new StringBuilder();
        for (RelationalProperty property : properties) {
            if (!property.getValue().isInsertable()) {
                continue;
            }
            PropertySqlPart propertySqlPart = columnSqlParts.get(property.getName());
            if (property.getValue().getDefaultValue() == null) {
                String mid = propertySqlPart.valuePlaceholder + ",";
                if (nullable && property.isNullable()) {
                    insertValues.append(SqlPart.tagNotNull(property.getName(), property.isIgnoreEmptyChar(), mid));
                } else {
                    insertValues.append(mid).append(LINE_SEPARATOR);
                }
            } else {
                insertValues.append(columnDefaultValue(property.getValue()));
            }
        }
        return SqlPart.tagTrim(insertValues.toString(), "(", null, ")", ",");
    }

    public String updateColumnValue(boolean nullable, String prefix) {
        StringBuilder sqlBuilder = new StringBuilder();
        for (RelationalProperty property : properties) {
            if (property.isPrimaryKey()) {
                continue;
            }
            String mid = propertyUpdateColumnValue(property, prefix) + ",";
            if (nullable) {
                sqlBuilder.append(SqlPart.tagNotNull((StringUtils.isNotBlank(prefix) ? prefix + DOT : EMPTY) + property.getName()
                        , property.isIgnoreEmptyChar(), mid));
            } else {
                sqlBuilder.append(mid).append(LINE_SEPARATOR);
            }
        }
        return sqlBuilder.toString();
    }

    public String updateColumnValueChoseKey(String prefix) {
        StringBuilder sqlBuilder = new StringBuilder();
        for (RelationalProperty property : properties) {
            if (property.isPrimaryKey()) {
                continue;
            }
            String mid = propertyUpdateColumnValue(property, prefix);
            String tagIf = SqlPart.tagIf("choseKeys.contains('" + property.getName() + "')", mid);
            sqlBuilder.append(tagIf);
        }
        return sqlBuilder.toString();
    }

    private String propertyUpdateColumnValue(RelationalProperty property, String prefix) {
        PropertySqlPart propertySqlPart = columnSqlParts.get(property.getName());
        StringBuilder midBuilder = new StringBuilder();
        midBuilder.append(propertySqlPart.safeColumn);
        if (property.isVersion()) {
            midBuilder.append(EQUALS).append(SqlPart.placeholder(property.getName() + "Next"));
        } else {
            midBuilder.append(EQUALS).append(SqlPart.placeholder(property, prefix));
        }
        return midBuilder.toString();
    }

    public String logicDeleteColumnValue() {
        StringBuilder sqlBuilder = new StringBuilder();
        for (RelationalProperty property : properties) {
            if (property.isLogicDelete()) {
                PropertySqlPart propertySqlPart = columnSqlParts.get(property.getName());
                LogicDeleteValue value = property.getAsLogicDeleteValue();
                Serializable logicDeleteValue = value.getLogicDeleteValue();
                String deleteValueStr = dialect.getLiteralValueHandler().convert(logicDeleteValue);
                sqlBuilder.append(propertySqlPart.safeColumn).append(EQUALS).append(deleteValueStr).append(",")
                        .append(LINE_SEPARATOR);
            }
        }
        return sqlBuilder.toString();
    }

    public String columnDefaultValue(Value<? extends Serializable> value) {
        Serializable defaultValue = value.getDefaultValue();
        String defaultValueStr = dialect.getLiteralValueHandler().convert(defaultValue);
        return defaultValueStr + "," + LINE_SEPARATOR;
    }

}
