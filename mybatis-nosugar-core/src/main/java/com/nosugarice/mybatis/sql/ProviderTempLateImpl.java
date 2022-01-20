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

package com.nosugarice.mybatis.sql;

import com.nosugarice.mybatis.builder.SqlSourceScriptBuilder;
import com.nosugarice.mybatis.config.EntityMetadata;
import com.nosugarice.mybatis.criteria.CriteriaDelete;
import com.nosugarice.mybatis.criteria.CriteriaQuery;
import com.nosugarice.mybatis.criteria.CriteriaUpdate;
import com.nosugarice.mybatis.criteria.select.QueryStructure;
import com.nosugarice.mybatis.criteria.update.UpdateStructure;
import com.nosugarice.mybatis.criteria.where.ColumnCriterion;
import com.nosugarice.mybatis.criteria.where.GroupCriterion;
import com.nosugarice.mybatis.criteria.where.WhereStructure;
import com.nosugarice.mybatis.criteria.where.criterion.EqualTo;
import com.nosugarice.mybatis.criteria.where.criterion.In;
import com.nosugarice.mybatis.dialect.Dialect;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.sql.ParameterBind.ParameterColumnBind;
import com.nosugarice.mybatis.sql.render.EntitySQLRender;
import com.nosugarice.mybatis.sql.render.PreparedSQLVisitor;
import com.nosugarice.mybatis.sql.render.QuerySQLRender;
import com.nosugarice.mybatis.sql.render.WhereSQLRender;
import com.nosugarice.mybatis.util.Preconditions;
import com.nosugarice.mybatis.util.StringJoinerBuilder;
import com.nosugarice.mybatis.util.StringUtils;
import org.apache.ibatis.session.RowBounds;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.nosugarice.mybatis.sql.Placeholder.FROM_TABLE_P;
import static com.nosugarice.mybatis.sql.Placeholder.TABLE_P;
import static com.nosugarice.mybatis.sql.SQLConstants.AND;
import static com.nosugarice.mybatis.sql.SQLConstants.DELETE;
import static com.nosugarice.mybatis.sql.SQLConstants.DISTINCT;
import static com.nosugarice.mybatis.sql.SQLConstants.EMPTY;
import static com.nosugarice.mybatis.sql.SQLConstants.EQUALS_TO;
import static com.nosugarice.mybatis.sql.SQLConstants.INSERT;
import static com.nosugarice.mybatis.sql.SQLConstants.INTO;
import static com.nosugarice.mybatis.sql.SQLConstants.SELECT;
import static com.nosugarice.mybatis.sql.SQLConstants.SET;
import static com.nosugarice.mybatis.sql.SQLConstants.SPACE;
import static com.nosugarice.mybatis.sql.SQLConstants.UPDATE;
import static com.nosugarice.mybatis.sql.SQLConstants.VALUES;
import static com.nosugarice.mybatis.sql.SQLConstants.WHERE;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/22
 */
public class ProviderTempLateImpl implements ProviderTempLate {

    private final EntityMetadata entityMetadata;
    private final Dialect dialect;
    private final EntitySQLPart entitySqlPart;
    private final EntitySQLRender sqlRender;

    public ProviderTempLateImpl(EntityMetadata entityMetadata, Dialect dialect) {
        this.entityMetadata = entityMetadata;
        this.dialect = dialect;
        this.entitySqlPart = new EntitySQLPart(entityMetadata, dialect);
        this.sqlRender = new EntitySQLRender.Builder()
                .withTable(entityMetadata.getRelationalEntity().getTable().getName())
                .withSupportDynamicTableName(entityMetadata.getSupports().isSupportDynamicTableName())
                .build();
    }

    @Override
    public <ID> SqlAndParameterBind selectById(ID id) {
        return byIdBind(id, where -> getSelectSql(false, where));
    }

    @Override
    public <ID> SqlAndParameterBind selectByIds(Collection<ID> ids) {
        return byIdsBind(ids, where -> getSelectSql(false, where));
    }

    private String getSelectSql(boolean distinct, String where) {
        where = buildWhereSql(where);
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(SELECT)
                .withElements(distinct, DISTINCT)
                .withElements(StringUtils.trim(entitySqlPart.selectResult, null, ","))
                .withElements(FROM_TABLE_P)
                .withElements(WHERE)
                .withElements(StringUtils.trim(where, AND, null))
                .build();
        sql = sqlRender.renderWithTableAlias(sql, false);
        return sql;
    }

    @Override
    public <T> SqlAndParameterBind selectList(CriteriaQuery<T, ?> criteria) {
        if (criteria == null) {
            return selectAll();
        }
        Preconditions.checkArgument(criteria instanceof QueryStructure, "不支持的查询结构类型.");
        QueryStructure<?> structure = (QueryStructure<?>) criteria;
        SqlAndParameterBind sqlAndParameterBind = structureWhereBind(structure, null);
        String whereSql = buildWhereSql(sqlAndParameterBind.getSql());
        String sql;
        if (structure.isSimple()) {
            sql = StringJoinerBuilder.createSpaceJoin()
                    .withElements(SELECT)
                    .withElements(StringUtils.trim(entitySqlPart.selectResult, null, ","))
                    .withElements(FROM_TABLE_P)
                    .withElements(StringUtils.isNotBlank(whereSql), WHERE)
                    .withElements(StringUtils.trim(whereSql, AND, null))
                    .build();
            sql = sqlRender.renderWithTableAlias(sql, false);
        } else {
            QuerySQLRender render = structure.getRender(sqlRender);
            String result = StringJoinerBuilder.createSpaceJoin()
                    .withElements(!structure.getColumnSelections().isPresent()
                            && !structure.getFunctionSelections().isPresent(), entitySqlPart.selectResult)
                    .withElements(structure.getColumnSelections().isPresent(), render.renderColumnSelect())
                    .withElements(structure.getFunctionSelections().isPresent(), render.renderFunctionSelect())
                    .withElements(structure.getJoinCriteria().isPresent(), render.renderJoinSelect())
                    .build();
            sql = StringJoinerBuilder.createSpaceJoin()
                    .withElements(SELECT)
                    .withElements(structure.isDistinct(), DISTINCT)
                    .withElements(StringUtils.trim(result, null, ","))
                    .withElements(render.renderFrom())
                    .withElements(structure.getJoinCriteria().isPresent(), render.renderJoinFom())
                    .withElements(StringUtils.isNotBlank(whereSql), WHERE)
                    .withElements(StringUtils.trimParenthesis(StringUtils.trim(whereSql, AND, null)))
                    .withElements(structure.getGroupBy().isPresent(), render.renderGroupBy())
                    .withElements(structure.getHaving().isPresent(), render.renderHaving())
                    .withElements(structure.getOrderBy().isPresent(), render.renderOrderBy())
                    .build();
            sql = sqlRender.renderWithTableAlias(sql, structure.getJoinCriteria().isPresent());
        }

        if (structure.getLimit().isPresent()) {
            RowBounds rowBounds = structure.getLimit().get();
            sql = dialect.getLimitHandler().processSql(sql, rowBounds.getOffset(), rowBounds.getLimit());
        }
        sqlAndParameterBind.setSql(sql);
        return sqlAndParameterBind;
    }

    private SqlAndParameterBind selectAll() {
        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind();
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(SELECT)
                .withElements(StringUtils.trim(entitySqlPart.selectResult, null, ","))
                .withElements(FROM_TABLE_P)
                .withElements(entityMetadata.getSupports().isSupportLogicDelete(), WHERE, entitySqlPart.selectParameterLogicDelete)
                .build();
        sql = sqlRender.renderWithTableAlias(sql, false);
        sqlAndParameterBind.setSql(sql);
        return sqlAndParameterBind;
    }

    @Override
    public <T> SqlAndParameterBind insert(T entity) {
        StringJoinerBuilder columnJoin = StringJoinerBuilder.createSpaceJoin().withDelimiter(", ").withPrefix("(").withSuffix(")");
        StringJoinerBuilder valueJoin = StringJoinerBuilder.createSpaceJoin().withDelimiter(", ").withPrefix("(").withSuffix(")");

        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind();
        if (entity == SqlSourceScriptBuilder.PLACEHOLDER_OBJECT) {
            for (RelationalProperty property : entityMetadata.getRelationalEntity().getProperties()) {
                if (!property.getValue().isInsertable()) {
                    continue;
                }
                sqlAndParameterBind.bind(null, property.getColumn(), entityMetadata.getEntityClass()).canHandle();
                columnJoin.withElements(property.getColumn());
                valueJoin.withElements("?");
            }
        } else {
            Map<String, Object> columnValues = getEntityColumnValues(entity, true);
            for (RelationalProperty property : entityMetadata.getRelationalEntity().getProperties()) {
                if (!property.getValue().isInsertable()) {
                    continue;
                }
                sqlAndParameterBind.bind(columnValues.get(property.getColumn()), property.getColumn(), entityMetadata.getEntityClass()).canHandle();
                columnJoin.withElements(property.getColumn());
                valueJoin.withElements("?");
            }
        }
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(INSERT, INTO)
                .withElements(TABLE_P)
                .withElements(columnJoin.build())
                .withElements(VALUES)
                .withElements(valueJoin.build())
                .build();
        sql = sqlRender.renderWithTableAlias(sql, false);
        sqlAndParameterBind.setSql(sql);
        if (entity == SqlSourceScriptBuilder.PLACEHOLDER_OBJECT) {
            sqlAndParameterBind.setParameterHandle((t, parameterColumnBinds, boundSql) -> {
                Map<String, Object> columnValues = getEntityColumnValues(t, true);
                for (ParameterColumnBind parameterColumnBind : parameterColumnBinds) {
                    boundSql.setAdditionalParameter(parameterColumnBind.getParameter(), columnValues.get(parameterColumnBind.getColumn()));
                }
                return null;
            });
        }
        return sqlAndParameterBind;
    }

    @Override
    public <T> SqlAndParameterBind insertNullable(T entity) {
        StringJoinerBuilder columnJoin = StringJoinerBuilder.createSpaceJoin().withDelimiter(", ").withPrefix("(").withSuffix(")");
        StringJoinerBuilder valueJoin = StringJoinerBuilder.createSpaceJoin().withDelimiter(", ").withPrefix("(").withSuffix(")");

        Map<String, Object> columnValues = getEntityColumnValues(entity, false);
        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind();
        for (RelationalProperty property : entityMetadata.getRelationalEntity().getProperties()) {
            if (!property.getValue().isInsertable()) {
                continue;
            }
            Object value = columnValues.get(property.getColumn());
            if (value == null) {
                if (!property.isNullable() && property.getValue().insertHandler() == null) {
                    throw new IllegalArgumentException("[" + property.getName() + "]不能为空.");
                }
            }
            sqlAndParameterBind.bind(value, property.getColumn(), entityMetadata.getEntityClass()).canHandle();
            columnJoin.withElements(property.getColumn());
            valueJoin.withElements("?");
        }
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(INSERT, INTO)
                .withElements(TABLE_P)
                .withElements(columnJoin.build())
                .withElements(VALUES)
                .withElements(valueJoin.build())
                .build();
        sql = sqlRender.renderWithTableAlias(sql, false);
        sqlAndParameterBind.setSql(sql);
        return sqlAndParameterBind;
    }

    @Override
    public <T> SqlAndParameterBind updateById(T entity, boolean nullable) {
        return updateById(entity, null, nullable);
    }

    @Override
    public <T> SqlAndParameterBind updateByIdChoseProperty(T entity, Set<String> choseProperties) {
        Set<String> relationalProperties = entityMetadata.getRelationalEntity().getProperties().stream()
                .filter(relationalProperty -> choseProperties.contains(relationalProperty.getName()) || relationalProperty.isVersion())
                .map(RelationalProperty::getColumn)
                .collect(Collectors.toSet());
        return updateById(entity, relationalProperties, false);
    }

    private <T> SqlAndParameterBind updateById(T entity, Set<String> includeColumns, boolean nullable) {
        Map<String, Object> values = entityToUpdateValues(entity, includeColumns, nullable);
        SqlAndParameterBind sqlAndParameterBind = updateValueBind(values);
        String updateIdWhere = buildIdBind(entity, sqlAndParameterBind.getParameterBind());
        return update(values, sqlAndParameterBind.getSql(), updateIdWhere, sqlAndParameterBind.getParameterBind());
    }

    @Override
    public <T> SqlAndParameterBind update(CriteriaUpdate<T, ?> criteria) {
        Preconditions.checkArgument(criteria instanceof UpdateStructure, "不支持的查询结构类型.");
        UpdateStructure structure = (UpdateStructure) criteria;
        Map<String, Object> values = structure.getSetValues();
        SqlAndParameterBind sqlAndParameterBind = updateValueBind(values);
        SqlAndParameterBind whereBind = structureWhereBind(structure, sqlAndParameterBind.getParameterBind());
        return update(values, sqlAndParameterBind.getSql(), whereBind.getSql(), sqlAndParameterBind.getParameterBind());
    }

    private <T> Map<String, Object> entityToUpdateValues(T entity, Set<String> includeColumns, boolean nullable) {
        Map<String, Object> columnValues = getEntityColumnValues(entity, nullable);
        if (includeColumns != null) {
            columnValues.entrySet().removeIf(entry -> !includeColumns.contains(entry.getKey()));
        }
        return columnValues;
    }

    private SqlAndParameterBind updateValueBind(Map<String, Object> setValues) {
        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind();
        StringJoinerBuilder columnValueJoin = StringJoinerBuilder.createSpaceJoin();
        for (Map.Entry<String, Object> entry : setValues.entrySet()) {
            RelationalProperty property = entityMetadata.getPropertyByColumnName(entry.getKey());
            if (!property.getValue().isUpdateable()) {
                continue;
            }
            sqlAndParameterBind.bind(entry.getValue(), property.getColumn(), entityMetadata.getEntityClass()).canHandle();
            columnValueJoin.withElements(property.getColumn(), EQUALS_TO, "?", ",");
        }
        sqlAndParameterBind.setSql(columnValueJoin.build());
        return sqlAndParameterBind;
    }

    private SqlAndParameterBind update(Map<String, Object> setValues, String updateValue, String where, ParameterBind parameterBind) {
        if (entityMetadata.getSupports().isSupportVersion()) {
            Object version = setValues.get(entityMetadata.getVersionProperty().getColumn());
            if (version != null) {
                String versionWhere = buildVersionBind(version, parameterBind);
                where = where + SPACE + versionWhere;
            }
        }
        where = buildWhereSql(where);
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(UPDATE, TABLE_P, SET)
                .withElements(StringUtils.trim(updateValue, null, ","))
                .withElements(StringUtils.isNotBlank(where), WHERE)
                .withElements(StringUtils.trim(where, AND, null))
                .build();
        sql = sqlRender.renderWithTableAlias(sql, false);
        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind(parameterBind);
        sqlAndParameterBind.setSql(sql);
        return sqlAndParameterBind;
    }

    @Override
    public <ID> SqlAndParameterBind deleteById(ID id, boolean logicDelete) {
        return byIdBind(id, logicDelete ? this::getLogicDeleteSql : this::getDeleteSql);
    }

    @Override
    public <ID> SqlAndParameterBind deleteByIds(Collection<ID> ids, boolean logicDelete) {
        return byIdsBind(ids, logicDelete ? this::getLogicDeleteSql : this::getDeleteSql);
    }

    @Override
    public <T> SqlAndParameterBind delete(CriteriaDelete<T, ?> criteria, boolean logicDelete) {
        return delete(criteria, logicDelete ? this::getLogicDeleteSql : this::getDeleteSql);
    }

    private <T> SqlAndParameterBind delete(CriteriaDelete<T, ?> criteria, Function<String, String> deleteSqlHandler) {
        Preconditions.checkArgument(criteria instanceof WhereStructure, "不支持的查询结构类型.");
        WhereStructure structure = (WhereStructure) criteria;
        SqlAndParameterBind sqlAndParameterBind = structureWhereBind(structure, null);
        sqlAndParameterBind.setSql(deleteSqlHandler.apply(sqlAndParameterBind.getSql()));
        return sqlAndParameterBind;
    }

    private String getDeleteSql(String where) {
        where = buildWhereSql(where);
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(DELETE)
                .withElements(FROM_TABLE_P)
                .withElements(WHERE)
                .withElements(StringUtils.trim(where, AND, null))
                .build();
        sql = sqlRender.renderWithTableAlias(sql, false);
        return sql;
    }

    private String getLogicDeleteSql(String where) {
        where = buildWhereSql(where);
        RelationalProperty logicDeleteProperty = entityMetadata.getLogicDeleteProperty();
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(UPDATE)
                .withElements(TABLE_P, SET)
                .withElements(logicDeleteProperty.getColumn()
                        , EQUALS_TO, literalValue(logicDeleteProperty.getAsLogicDeleteValue().getLogicDeleteValue()))
                .withElements(WHERE)
                .withElements(StringUtils.trim(where, AND, null))
                .build();
        sql = sqlRender.renderWithTableAlias(sql, false);
        return sql;
    }

    @Override
    public SqlAndParameterBind provideJpaFind(boolean distinct, String whereSql, String orderBy, Integer limit) {
        String sql = getSelectSql(distinct, whereSql);
        if (StringUtils.isNotBlank(orderBy)) {
            sql = sql + SPACE + sqlRender.renderWithTableAlias(orderBy, false);
        }
        if (limit > 0) {
            sql = dialect.getLimitHandler().processSql(sql, 0, limit);
        }
        return new SqlAndParameterBind(sql);
    }

    @Override
    public SqlAndParameterBind provideJpaCount(String where) {
        where = buildWhereSql(where);
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(SELECT)
                .withElements("COUNT(*)")
                .withElements(FROM_TABLE_P)
                .withElements(WHERE)
                .withElements(StringUtils.trim(where, AND, null))
                .build();
        sql = sqlRender.renderWithTableAlias(sql, false);
        return new SqlAndParameterBind(sql);
    }

    @Override
    public SqlAndParameterBind provideJpaExists(String where) {
        where = buildWhereSql(where);
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(SELECT)
                .withElements("1")
                .withElements(FROM_TABLE_P)
                .withElements(WHERE)
                .withElements(StringUtils.trim(where, AND, null))
                .build();
        sql = sqlRender.renderWithTableAlias(sql, false);
        sql = dialect.getLimitHandler().processSql(sql, 0, 1);
        return new SqlAndParameterBind(sql);
    }

    @Override
    public SqlAndParameterBind provideJpaDelete(String whereSql) {
        return new SqlAndParameterBind(getDeleteSql(whereSql));
    }

    @Override
    public SqlAndParameterBind provideJpaLogicDelete(String whereSql) {
        return new SqlAndParameterBind(getLogicDeleteSql(whereSql));
    }

    private <ID> SqlAndParameterBind byIdBind(ID id, Function<String, String> sqlHandle) {
        SqlAndParameterBind sqlAndParameterBind = getByIdBind(id);
        String sql = sqlHandle.apply(sqlAndParameterBind.getSql());
        sqlAndParameterBind.setSql(sql);
        return sqlAndParameterBind;
    }

    private <ID> SqlAndParameterBind byIdsBind(Collection<ID> ids, Function<String, String> sqlHandle) {
        ColumnCriterion<Collection<ID>> criterion = new In<>(entityMetadata.getPrimaryKeyProperty().getColumn(), ids);
        SqlAndParameterBind sqlAndParameterBind = criterion.accept(new PreparedSQLVisitor(entityMetadata.getEntityClass()));
        String sql = sqlHandle.apply(sqlAndParameterBind.getSql());
        sqlAndParameterBind.setSql(sql);
        return sqlAndParameterBind;
    }

    private <ID> SqlAndParameterBind getByIdBind(ID id) {
        String idColumn = entityMetadata.getPrimaryKeyProperty().getColumn();
        ColumnCriterion<ID> criterion = new EqualTo<>(idColumn, id);
        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind(criterion.getSql());
        sqlAndParameterBind.bind(id, idColumn, entityMetadata.getEntityClass());
        if (id == SqlSourceScriptBuilder.PLACEHOLDER_OBJECT) {
            sqlAndParameterBind.setParameterHandle((idValue, parameterColumnBinds, boundSql) -> {
                parameterColumnBinds.forEach(parameterColumnBind -> boundSql.setAdditionalParameter(parameterColumnBind.getParameter(), idValue));
                return null;
            });
        }
        return sqlAndParameterBind;
    }

    private <T> String buildIdBind(T entity, ParameterBind parameterBind) {
        RelationalProperty idProperty = entityMetadata.getPrimaryKeyProperty();
        Object id = idProperty.getValue(entity);
        return buildEqualBind(id, idProperty.getColumn(), parameterBind);
    }

    private <Version> String buildVersionBind(Version version, ParameterBind parameterBind) {
        return buildEqualBind(version, entityMetadata.getVersionProperty().getColumn(), parameterBind);
    }

    private String buildEqualBind(Object value, String column, ParameterBind parameterBind) {
        ColumnCriterion<?> criterion = new EqualTo<>(column, value);
        parameterBind.bindValue(value, column, entityMetadata.getEntityClass());
        return criterion.getSql();
    }

    private String buildWhereSql(String where) {
        return entityMetadata.getSupports().isSupportLogicDelete()
                ? where + SPACE + AND + SPACE + entitySqlPart.selectParameterLogicDelete : where;
    }

    private SqlAndParameterBind structureWhereBind(WhereStructure structure, ParameterBind parameterBind) {
        parameterBind = parameterBind == null ? new ParameterBind() : parameterBind;
        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind(parameterBind);
        String whereSql = EMPTY;
        if (structure instanceof QueryStructure) {
            QueryStructure<?> queryStructure = (QueryStructure<?>) structure;
            QuerySQLRender render = queryStructure.getRender(sqlRender);
            if (queryStructure.getCriterion().map(GroupCriterion::hasCriterion).orElse(false)) {
                whereSql = render.renderWhere(parameterBind);
            }
            if (queryStructure.getJoinCriteria().isPresent()) {
                whereSql = SQLPart.merge(whereSql, render.renderJoinWhere(parameterBind));
            }
        } else {
            WhereSQLRender render = new WhereSQLRender(structure);
            whereSql = render.renderWhere(parameterBind);
        }
        sqlAndParameterBind.setSql(whereSql);
        return sqlAndParameterBind;
    }

    private <T> Map<String, Object> getEntityColumnValues(T entity, boolean nullable) {
        Map<String, Object> columnValues = new LinkedHashMap<>(entityMetadata.getRelationalEntity().getProperties().size());
        for (RelationalProperty property : entityMetadata.getRelationalEntity().getProperties()) {
            Object value = property.getValue(entity);
            if (value != null || !nullable) {
                columnValues.put(property.getColumn(), value);
            }
        }
        return columnValues;
    }

    private String literalValue(Serializable value) {
        return dialect.getLiteralValueHandler().convert(value);
    }

}
