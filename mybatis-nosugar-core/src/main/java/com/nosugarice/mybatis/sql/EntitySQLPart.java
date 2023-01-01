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

package com.nosugarice.mybatis.sql;

import com.nosugarice.mybatis.config.EntityMetadata;
import com.nosugarice.mybatis.dialect.Dialect;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.util.StringJoinerBuilder;

import java.io.Serializable;

import static com.nosugarice.mybatis.sql.SQLConstants.AS_;
import static com.nosugarice.mybatis.sql.SQLConstants.EQUALS_TO;
import static com.nosugarice.mybatis.sql.SQLConstants.IS_NULL;
import static com.nosugarice.mybatis.sql.SQLConstants.SPACE;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/1/1
 */
public class EntitySQLPart {

    private final EntityMetadata entityMetadata;
    private final Dialect dialect;

    public String selectResult;
    public String selectParameterLogicDelete;

    public EntitySQLPart(EntityMetadata entityMetadata, Dialect dialect) {
        this.entityMetadata = entityMetadata;
        this.dialect = dialect;
        this.selectResult = selectResult();
        this.selectParameterLogicDelete = selectParameterLogicDelete();
    }

    private String selectResult() {
        StringBuilder sqlBuilder = new StringBuilder();
        for (RelationalProperty property : entityMetadata.getRelationalEntity().getProperties()) {
            String safeColumn = SQLPart.safeColumnName(property.getColumn(), dialect);
            if (property.getName().equals(property.getColumn())) {
                sqlBuilder.append(Placeholder.columnAliasState(safeColumn));
            } else {
                sqlBuilder.append(Placeholder.columnAliasState(safeColumn))
                        .append(AS_).append("\"").append(property.getName()).append("\"");
            }
            sqlBuilder.append(",").append(SPACE);
        }
        return sqlBuilder.toString();
    }

    private String selectParameterLogicDelete() {
        if (entityMetadata.getSupports().isSupportLogicDelete()) {
            RelationalProperty logicDeleteProperty = entityMetadata.getLogicDeleteProperty();
            StringJoinerBuilder joinerBuilder = StringJoinerBuilder.createSpaceJoin()
                    .withElements(Placeholder.columnAliasState(SQLPart.safeColumnName(logicDeleteProperty.getColumn(), dialect)));
            Serializable defaultValue = logicDeleteProperty.getValue().getDefaultValue();
            if (defaultValue == null) {
                joinerBuilder.withElements(IS_NULL);
            } else {
                joinerBuilder.withElements(EQUALS_TO, dialect.getLiteralValueHandler().convert(defaultValue));
            }
            return joinerBuilder.build();
        }
        return null;
    }

}
