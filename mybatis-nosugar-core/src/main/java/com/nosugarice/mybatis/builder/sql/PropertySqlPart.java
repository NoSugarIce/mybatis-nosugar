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

import com.nosugarice.mybatis.dialect.Dialect;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.util.StringUtils;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/1/1
 */
public class PropertySqlPart implements SqlPart {

    protected final RelationalProperty property;
    protected final String column;
    protected final String safeColumn;
    protected final String propertyName;
    protected final String columnVariable;
    protected final String resultItem;
    protected final String assignJdbcType;
    protected final String assignTypeHandler;
    protected final String valuePlaceholder;

    public PropertySqlPart(RelationalProperty property, Dialect dialect) {
        this.property = property;
        this.column = property.getColumn().getName();
        this.safeColumn = SqlPart.safeColumnName(column, dialect);
        this.propertyName = property.getName();
        this.columnVariable = Placeholder.ALIAS_STATE + safeColumn;
        this.resultItem = columnVariable + SPACE + AS + "\"" + property.getName() + "\"";
        this.assignJdbcType = SqlPart.assignJdbcType(property.getColumn().getJdbcType());
        this.assignTypeHandler = SqlPart.assignTypeHandler(property.getTypeHandler());
        this.valuePlaceholder = SqlPart.placeholder(property.getName(), null, assignJdbcType, assignTypeHandler);
    }

    public String valueItem(String prefix, String mark) {
        String valueItem = columnVariable + EQUALS + SqlPart.placeholder(propertyName, prefix, assignJdbcType, assignTypeHandler);
        if (StringUtils.isNotBlank(mark)) {
            valueItem = mark + valueItem;
        }
        return valueItem + SPACE;
    }

    public RelationalProperty getProperty() {
        return property;
    }

    public String getColumn() {
        return column;
    }

    public String getSafeColumn() {
        return safeColumn;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getColumnVariable() {
        return columnVariable;
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

    public String getValuePlaceholder() {
        return valuePlaceholder;
    }
}
