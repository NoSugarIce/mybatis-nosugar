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

import com.nosugarice.mybatis.builder.MapperMetadata;
import com.nosugarice.mybatis.mapping.RelationalEntity;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.sql.SqlTempLateService;

/**
 * 部分直接str ++ 了, 直观
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/12/2
 */
public class SqlTempLateServiceImpl implements SqlTempLateService, SqlPart {

    private final MapperMetadata mapperMetadata;
    private final RelationalEntity relationalEntity;
    private final EntitySqlPart entitySqlPart;
    private final SqlRender sqlRender;

    public SqlTempLateServiceImpl(MapperMetadata mapperMetadata, EntitySqlPart entitySqlPart) {
        this.mapperMetadata = mapperMetadata;
        this.relationalEntity = mapperMetadata.getRelationalEntity();
        this.entitySqlPart = entitySqlPart;
        this.sqlRender = new SqlRender.Builder()
                .withEntity(mapperMetadata.getRelationalEntity())
                .withSupports(mapperMetadata.getSupports())
                .withTableAlias(false)
                .build();
    }

    @Override
    public String selectById() {
        String sql = "SELECT\n" +
                "<trim suffixOverrides=\",\">\n" +
                entitySqlPart.selectResult +
                "</trim>\n" +
                Placeholder.FROM_TABLE + LINE_SEPARATOR +
                "<trim prefix=\"WHERE\" prefixOverrides=\"AND\">\n" +
                entitySqlPart.selectPrimaryKeyColumn +
                entitySqlPart.selectParameterLogicDelete +
                "</trim>";
        return sqlRender.renderWithTableAlias(sql, false);
    }

    @Override
    public String selectByIds() {
        RelationalProperty primaryKeyColumn = relationalEntity.getOnePrimaryKeyProperty();
        String sql = "SELECT\n" +
                "<trim suffixOverrides=\",\">\n" +
                entitySqlPart.selectResult +
                "</trim>\n" +
                Placeholder.FROM_TABLE + LINE_SEPARATOR +
                "<trim prefix=\"WHERE\" prefixOverrides=\"AND\">\n" +
                SqlPart.safeColumnName(primaryKeyColumn, mapperMetadata.getDialect()) + " IN \n" +
                "<foreach collection=\"ids\" item=\"id\" open=\"(\"  close=\")\" separator=\",\">\n" +
                "   #{id}\n" +
                "</foreach>\n" +
                entitySqlPart.selectParameterLogicDelete +
                "</trim>\n";
        return sqlRender.renderWithTableAlias(sql, false);
    }

    @Override
    public String selectList() {
        final String simpleQuery = "SELECT\n" +
                "<trim suffixOverrides=\",\">\n" +
                entitySqlPart.selectResult +
                "</trim>\n" +
                Placeholder.FROM_WITH_ALIAS + LINE_SEPARATOR +
                "<trim prefix=\"WHERE\" prefixOverrides=\"AND|OR\">\n" +
                entitySqlPart.selectParameter +
                entitySqlPart.selectParameterLogicDelete +
                "</trim>\n";

        final String complexQuery = "SELECT\n" +
                "<if test=\"criteria.distinct\">\n" +
                "   DISTINCT\n" +
                "</if>\n" +
                "<trim suffixOverrides=\",\">\n" +
                "<choose>\n" +
                "<when test=\"criteria.chooseResult\">\n" +
                "${criteria.chooseResultSql}" +
                "</when>\n" +
                "<otherwise>\n" +
                entitySqlPart.selectResult +
                "</otherwise>\n" +
                "</choose>\n" +
                "${criteria.expandColumnSql}\n" +
                "</trim>\n" +
                Placeholder.FROM_WITH_ALIAS + LINE_SEPARATOR +
                "<trim prefix=\"WHERE\" prefixOverrides=\"AND|OR\">\n" +
                entitySqlPart.selectParameter +
                "${criteria.criterionSql}\n" +
                entitySqlPart.selectParameterLogicDelete +
                "</trim>\n" +
                "${criteria.groupSql}\n" +
                "${criteria.havingSql}\n" +
                "${criteria.sortSql}\n";

        String sql = "" +
                "<choose>\n" +
                "  <when test=\"criteria.simple\">\n" +
                simpleQuery +
                "  </when>\n" +
                "  <otherwise>\n" +
                complexQuery +
                "  </otherwise>\n" +
                "</choose>\n";
        return sqlRender.renderWithTableAlias(sql, false);
    }

    @Override
    public String insert() {
        String sql = entitySqlPart.insertColumns(false) +
                entitySqlPart.insertValues(false);
        return sqlRender.renderWithTableAlias(sql, false);
    }

    @Override
    public String insertNullable() {
        String sql = entitySqlPart.insertColumns(true) +
                entitySqlPart.insertValues(true);
        return sqlRender.renderWithTableAlias(sql, false);
    }

    @Override
    public String insertBatch() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(entitySqlPart.insertColumns(false));
        sqlBuilder.append("<foreach  collection=\"list\" item=\"item\" separator=\",\" >").append(LINE_SEPARATOR);
        sqlBuilder.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\" >").append(LINE_SEPARATOR);
        for (RelationalProperty property : entitySqlPart.properties) {
            if (!property.getValue().isInsertable()) {
                continue;
            }
            if (property.getValue().getDefaultValue() == null) {
                sqlBuilder.append(SqlPart.placeholder(property, "item")).append(",").append(LINE_SEPARATOR);
            } else {
                sqlBuilder.append(entitySqlPart.columnDefaultValue(property.getValue()));
            }
        }
        sqlBuilder.append("</trim>").append(LINE_SEPARATOR);
        sqlBuilder.append("</foreach>").append(LINE_SEPARATOR);
        String sql = sqlBuilder.toString();
        return sqlRender.renderWithTableAlias(sql, false);
    }

    @Override
    public String updateById() {
        String sql = entitySqlPart.updateHead +
                "<trim suffixOverrides=\",\">\n" +
                entitySqlPart.updateColumnValue +
                "</trim>\n" +
                "<trim prefix=\"WHERE\" prefixOverrides=\"AND\">\n" +
                entitySqlPart.updatePrimaryKeyColumn +
                entitySqlPart.selectParameterVersion +
                entitySqlPart.updateParameterLogicDelete +
                "</trim>";
        return sqlRender.renderWithTableAlias(sql, false);
    }

    @Override
    public String updateByIdChoseKey() {
        String sql = entitySqlPart.updateHead +
                "<trim suffixOverrides=\",\">\n" +
                entitySqlPart.updateColumnValueChoseKey +
                "</trim>\n" +
                "<trim prefix=\"WHERE\" prefixOverrides=\"AND\">\n" +
                entitySqlPart.updatePrimaryKeyColumn +
                entitySqlPart.selectParameterVersion +
                entitySqlPart.updateParameterLogicDelete +
                "</trim>";
        return sqlRender.renderWithTableAlias(sql, false);
    }

    @Override
    public String updateNullableById() {
        String sql = entitySqlPart.updateHead +
                "<trim suffixOverrides=\",\">\n" +
                entitySqlPart.updateColumnValueNullable +
                "</trim>\n" +
                "<trim prefix=\"WHERE\" prefixOverrides=\"AND\">\n" +
                entitySqlPart.updatePrimaryKeyColumn +
                entitySqlPart.selectParameterVersionNullable +
                entitySqlPart.updateParameterLogicDelete +
                "</trim>";
        return sqlRender.renderWithTableAlias(sql, false);
    }

    @Override
    public String update() {
        String sql = entitySqlPart.updateHead +
                entitySqlPart.updateHead +
                "<trim suffixOverrides=\",\">\n" +
                entitySqlPart.updateColumnValue +
                "</trim>\n" +
                "<trim prefix=\"WHERE\" prefixOverrides=\"AND|OR\">\n" +
                entitySqlPart.selectParameter +
                "${criteria.criterionSql}\n" +
                entitySqlPart.selectParameterVersion +
                entitySqlPart.selectParameterLogicDelete +
                "</trim>\n";
        return sqlRender.renderWithTableAlias(sql, false);
    }

    @Override
    public String updateNullable() {
        String sql = entitySqlPart.updateHead +
                entitySqlPart.updateHead +
                "<trim suffixOverrides=\",\">\n" +
                entitySqlPart.updateColumnValueNullable +
                "</trim>\n" +
                "<trim prefix=\"WHERE\" prefixOverrides=\"AND|OR\">\n" +
                entitySqlPart.selectParameter +
                "${criteria.criterionSql}\n" +
                entitySqlPart.selectParameterVersionNullable +
                entitySqlPart.selectParameterLogicDelete +
                "</trim>\n";
        return sqlRender.renderWithTableAlias(sql, false);
    }

    @Override
    public String deleteById() {
        String sql = entitySqlPart.updateHead +
                entitySqlPart.deleteHead +
                "<trim prefix=\"WHERE\" prefixOverrides=\"AND\">\n" +
                entitySqlPart.selectPrimaryKeyColumn +
                entitySqlPart.selectParameterLogicDelete +
                "</trim>";
        return sqlRender.renderWithTableAlias(sql, false);
    }

    @Override
    public String deleteByIds() {
        RelationalProperty primaryKeyColumn = relationalEntity.getOnePrimaryKeyProperty();
        String sql = entitySqlPart.updateHead +
                entitySqlPart.deleteHead +
                "<trim prefix=\"WHERE\" prefixOverrides=\"AND\">\n" +
                AND + SqlPart.safeColumnName(primaryKeyColumn, mapperMetadata.getDialect()) + " IN \n" +
                "<foreach collection=\"ids\" item=\"id\" open=\"(\"  close=\")\" separator=\",\">\n" +
                "   #{id}\n" +
                "</foreach>\n" +
                entitySqlPart.selectParameterLogicDelete +
                "</trim>\n";
        return sqlRender.renderWithTableAlias(sql, false);
    }

    @Override
    public String delete() {
        String sql = entitySqlPart.updateHead +
                entitySqlPart.deleteHead +
                "<trim prefix=\"WHERE\" prefixOverrides=\"AND|OR\">\n" +
                entitySqlPart.selectParameter +
                "${criteria.criterionSql}\n" +
                entitySqlPart.selectParameterLogicDelete +
                "</trim>\n";
        return sqlRender.renderWithTableAlias(sql, false);
    }

    @Override
    public String logicDeleteById() {
        String sql = entitySqlPart.updateHead +
                entitySqlPart.updateHead +
                "<trim suffixOverrides=\",\">\n" +
                entitySqlPart.logicDeleteColumnValue +
                "</trim>\n" +
                "<trim prefix=\"WHERE\" prefixOverrides=\"AND\">\n" +
                entitySqlPart.updatePrimaryKeyColumn +
                entitySqlPart.updateParameterLogicDelete +
                "</trim>";
        return sqlRender.renderWithTableAlias(sql, false);
    }

    @Override
    public String logicDeleteByIds() {
        RelationalProperty primaryKeyColumn = relationalEntity.getOnePrimaryKeyProperty();
        String sql = entitySqlPart.updateHead +
                entitySqlPart.updateHead +
                "<trim suffixOverrides=\",\">\n" +
                entitySqlPart.logicDeleteColumnValue +
                "</trim>\n" +
                "<trim prefix=\"WHERE\" prefixOverrides=\"AND\">\n" +
                AND + SqlPart.safeColumnName(primaryKeyColumn, mapperMetadata.getDialect()) + " IN \n" +
                "<foreach collection=\"ids\" item=\"id\" open=\"(\"  close=\")\" separator=\",\">\n" +
                "   #{id}\n" +
                "</foreach>\n" +
                entitySqlPart.updateParameterLogicDelete +
                "</trim>";
        return sqlRender.renderWithTableAlias(sql, false);
    }

    @Override
    public String logicDelete() {
        String sql = entitySqlPart.updateHead +
                entitySqlPart.updateHead +
                "<trim suffixOverrides=\",\">\n" +
                entitySqlPart.logicDeleteColumnValue +
                "</trim>\n" +
                "<trim prefix=\"WHERE\" prefixOverrides=\"AND|OR\">\n" +
                entitySqlPart.selectParameter +
                "${criteria.criterionSql}\n" +
                entitySqlPart.selectParameterLogicDelete +
                "</trim>\n";
        return sqlRender.renderWithTableAlias(sql, false);
    }

    @Override
    public String provideFind() {
        String sql = "SELECT\n" +
                "<trim suffixOverrides=\",\">\n" +
                entitySqlPart.selectResult +
                "</trim>\n" +
                Placeholder.FROM_TABLE + "\n" +
                "<trim prefix=\"WHERE\" prefixOverrides=\"AND|OR\">\n" +
                Placeholder.JPA_WHERE + "\n" +
                entitySqlPart.selectParameterLogicDelete +
                "</trim>";
        return sqlRender.renderWithTableAlias(sql, false);
    }

    @Override
    public String provideCount() {
        String sql = "SELECT COUNT(*) " +
                Placeholder.FROM_TABLE + "\n" +
                "<trim prefix=\"WHERE\" prefixOverrides=\"AND|OR\">\n" +
                Placeholder.JPA_WHERE +
                entitySqlPart.selectParameterLogicDelete +
                "</trim>";
        return sqlRender.renderWithTableAlias(sql, false);
    }

    @Override
    public String provideDelete() {
        String sql = entitySqlPart.deleteHead +
                "<trim prefix=\"WHERE\" prefixOverrides=\"AND|OR\">\n" +
                Placeholder.JPA_WHERE + "\n" +
                entitySqlPart.selectParameterLogicDelete +
                "</trim>\n";
        return sqlRender.renderWithTableAlias(sql, false);
    }

}
