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

import com.nosugarice.mybatis.config.Constants;
import com.nosugarice.mybatis.config.EntityMetadata;
import com.nosugarice.mybatis.criteria.CriteriaDelete;
import com.nosugarice.mybatis.criteria.CriteriaQuery;
import com.nosugarice.mybatis.criteria.CriteriaUpdate;
import com.nosugarice.mybatis.criteria.criterion.ColumnCriterion;
import com.nosugarice.mybatis.criteria.criterion.GroupCriterion;
import com.nosugarice.mybatis.criteria.select.QueryStructure;
import com.nosugarice.mybatis.criteria.update.UpdateStructure;
import com.nosugarice.mybatis.criteria.where.WhereStructure;
import com.nosugarice.mybatis.criteria.where.criterion.EqualTo;
import com.nosugarice.mybatis.criteria.where.criterion.In;
import com.nosugarice.mybatis.criteria.where.criterion.IsNull;
import com.nosugarice.mybatis.dialect.Dialect;
import com.nosugarice.mybatis.mapping.RelationalEntity;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.sql.ParameterBind.ParameterColumnBind;
import com.nosugarice.mybatis.sql.render.EntitySQLRender;
import com.nosugarice.mybatis.sql.render.QuerySQLRender;
import com.nosugarice.mybatis.sql.render.WhereSQLRender;
import com.nosugarice.mybatis.sql.vistor.PreparedSQLVisitor;
import com.nosugarice.mybatis.sqlsource.SqlSourceScriptBuilder;
import com.nosugarice.mybatis.util.Preconditions;
import com.nosugarice.mybatis.util.StringJoinerBuilder;
import com.nosugarice.mybatis.util.StringUtils;
import org.apache.ibatis.session.RowBounds;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.nosugarice.mybatis.sql.Placeholder.FROM_TABLE_P;
import static com.nosugarice.mybatis.sql.Placeholder.TABLE_P;
import static com.nosugarice.mybatis.sql.SQLConstants.AND;
import static com.nosugarice.mybatis.sql.SQLConstants.AS_;
import static com.nosugarice.mybatis.sql.SQLConstants.DELETE;
import static com.nosugarice.mybatis.sql.SQLConstants.DISTINCT;
import static com.nosugarice.mybatis.sql.SQLConstants.EMPTY;
import static com.nosugarice.mybatis.sql.SQLConstants.EQUALS_TO;
import static com.nosugarice.mybatis.sql.SQLConstants.FOR_UPDATE;
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
    private final String selectResult;
    private final EntitySQLRender sqlRender;

    public ProviderTempLateImpl(EntityMetadata entityMetadata, Dialect dialect) {
        this.entityMetadata = entityMetadata;
        this.dialect = dialect;
        this.selectResult = selectResult();
        this.sqlRender = new EntitySQLRender.Builder()
                .withTable(entityMetadata.getRelationalEntity().getTable().getName()
                        , entityMetadata.getRelationalEntity().getTable().getSchema())
                .withSupportDynamicTableName(entityMetadata.getSupports().isSupportDynamicTableName())
                .build();
    }

    @Override
    public <ID> SqlAndParameterBind selectById(ID id) {
        return byIdBind(id, sqlAndParameterBind -> new SqlAndParameterBind(sqlAndParameterBind)
                .setSql(getSimpleSelectSql(false, sqlAndParameterBind.getSql())));
    }

    @Override
    public <ID> SqlAndParameterBind selectByIds(Collection<ID> ids) {
        return byIdsBind(ids, sqlAndParameterBind -> new SqlAndParameterBind(sqlAndParameterBind)
                .setSql(getSimpleSelectSql(false, sqlAndParameterBind.getSql())));
    }

    private String getSimpleSelectSql(boolean distinct, String where) {
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(SELECT)
                .withElements(distinct, DISTINCT)
                .withElements(StringUtils.trim(selectResult, null, ","))
                .withElements(FROM_TABLE_P)
                .withElements(WHERE)
                .withElements(StringUtils.trim(where, AND, null))
                .build();
        return sqlRender.render(sql);
    }

    @Override
    public <T> SqlAndParameterBind selectList(CriteriaQuery<T, ?, ?> criteria) {
        if (criteria == null) {
            return selectAll();
        }
        Preconditions.checkArgument(criteria instanceof QueryStructure, "不支持的查询结构类型.");
        QueryStructure structure = (QueryStructure) criteria;
        SqlAndParameterBind whereSqlAndParameterBind = structureWhereBind(structure, structure.getParameterBind(), true);
        String whereSql = whereSqlAndParameterBind.getSql();
        String sql;
        if (structure.isSimple()) {
            sql = StringJoinerBuilder.createSpaceJoin()
                    .withElements(SELECT)
                    .withElements(StringUtils.trim(selectResult, null, ","))
                    .withElements(FROM_TABLE_P)
                    .withElements(StringUtils.isNotBlank(whereSql), WHERE)
                    .withElements(StringUtils.trim(whereSql, AND, null))
                    .build();
            sql = sqlRender.render(sql);
        } else {
            QuerySQLRender render = structure.getRender(sqlRender);
            String result = StringJoinerBuilder.createSpaceJoin()
                    .withElements(!structure.getColumnSelections().isPresent()
                            && !structure.getFunctionSelections().isPresent(), selectResult)
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
                    .withElements(structure.isForUpdate(), FOR_UPDATE)
                    .build();
            sql = structure.getJoinCriteria().isPresent() ?
                    sqlRender.renderWithTableAlias(sql, structure.getTableAliasSequence().alias(structure)) : sqlRender.render(sql);
        }

        if (structure.getLimit().isPresent()) {
            RowBounds rowBounds = structure.getLimit().get();
            sql = sql.endsWith(FOR_UPDATE) ? sql.replace(FOR_UPDATE, SQLConstants.EMPTY) : sql;
            sql = dialect.getLimitHandler().applyLimit(sql, rowBounds.getOffset(), rowBounds.getLimit());
        }
        structure.getCountColumn().ifPresent(countColumn ->
                whereSqlAndParameterBind.addParameter(Constants.COUNT_COLUMN, countColumn));
        return whereSqlAndParameterBind.setSql(sql);
    }

    private SqlAndParameterBind selectAll() {
        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind();
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(SELECT)
                .withElements(StringUtils.trim(selectResult, null, ","))
                .withElements(FROM_TABLE_P)
                .withElements(entityMetadata.getSupports().isSupportLogicDelete(), WHERE
                        , StringUtils.trim(fixedWhereCondition(sqlAndParameterBind, false).getSql(), AND, null))
                .build();
        sqlAndParameterBind.setSql(sqlRender.render(sql));
        return sqlAndParameterBind;
    }

    @Override
    public <T> SqlAndParameterBind insert(T entity) {
        StringJoinerBuilder columnJoin = StringJoinerBuilder.createSpaceJoin().withDelimiter(", ").withPrefix("(").withSuffix(")");
        StringJoinerBuilder valueJoin = StringJoinerBuilder.createSpaceJoin().withDelimiter(", ").withPrefix("(").withSuffix(")");

        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind();
        if (entity == SqlSourceScriptBuilder.PLACEHOLDER_OBJECT) {
            for (RelationalProperty property : entityMetadata.getRelationalEntity().getProperties()) {
                if (property.getValue().isInsertable()) {
                    sqlAndParameterBind.bind(null, property.getColumn(), entityMetadata.getEntityClass());
                    columnJoin.withElements(safeColum(property.getColumn()));
                    valueJoin.withElements("?");
                }
            }
        } else {
            Map<String, Object> columnValues = getEntityColumnValues(entity, true);
            for (RelationalProperty property : entityMetadata.getRelationalEntity().getProperties()) {
                if (property.getValue().isInsertable()) {
                    Object value = columnValues.get(property.getColumn());
                    sqlAndParameterBind.bind(value, property.getColumn(), entityMetadata.getEntityClass());
                    columnJoin.withElements(safeColum(property.getColumn()));
                    valueJoin.withElements("?");
                }
            }
        }
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(INSERT, INTO)
                .withElements(TABLE_P)
                .withElements(columnJoin.build())
                .withElements(VALUES)
                .withElements(valueJoin.build())
                .build();
        if (entity == SqlSourceScriptBuilder.PLACEHOLDER_OBJECT) {
            sqlAndParameterBind.addParameterHandle((t, parameterColumnBinds, boundSql) -> {
                Map<String, Object> columnValues = getEntityColumnValues(t, true);
                for (ParameterColumnBind parameterColumnBind : parameterColumnBinds) {
                    boundSql.setAdditionalParameter(parameterColumnBind.getParameter()
                            , columnValues.get(parameterColumnBind.getColumn()));
                }
                return null;
            });
        }
        sql = sqlRender.render(sql);
        return sqlAndParameterBind.setSql(sql);
    }

    @Override
    public <T> SqlAndParameterBind insertNullable(T entity) {
        StringJoinerBuilder columnJoin = StringJoinerBuilder.createSpaceJoin().withDelimiter(", ").withPrefix("(").withSuffix(")");
        StringJoinerBuilder valueJoin = StringJoinerBuilder.createSpaceJoin().withDelimiter(", ").withPrefix("(").withSuffix(")");

        Map<String, Object> columnValues = getEntityColumnValues(entity, false);
        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind();
        for (RelationalProperty property : entityMetadata.getRelationalEntity().getProperties()) {
            if (property.getValue().isInsertable()) {
                Object value = columnValues.get(property.getColumn());
                if (value == null) {
                    if (!property.getValue().isInsertFill()) {
                        if (property.isNullable()) {
                            continue;
                        } else {
                            throw new IllegalArgumentException("[" + property.getName() + "]不能为空值.");
                        }
                    }
                }
                sqlAndParameterBind.bind(value, property.getColumn(), entityMetadata.getEntityClass());
                columnJoin.withElements(safeColum(property.getColumn()));
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
        sql = sqlRender.render(sql);
        return sqlAndParameterBind.setSql(sql);
    }

    @Override
    public <T> SqlAndParameterBind updateById(T entity, boolean nullable) {
        return updateById(entity, null, nullable);
    }

    @Override
    public <T> SqlAndParameterBind updateByIdChoseProperty(T entity, Set<String> choseProperties) {
        Set<String> updateColumns = entityMetadata.getRelationalEntity().getProperties().stream()
                .filter(relationalProperty -> choseProperties.contains(relationalProperty.getName()))
                .map(RelationalProperty::getColumn)
                .collect(Collectors.toSet());
        return updateById(entity, updateColumns, false);
    }

    private <T> SqlAndParameterBind updateById(T entity, Set<String> choseColumns, boolean nullable) {
        Map<String, Object> updateColumnValues = getEntityColumnValues(entity, nullable);
        if (choseColumns != null) {
            updateColumnValues.entrySet().removeIf(entry -> !choseColumns.contains(entry.getKey()));
        }

        Optional.of(entityMetadata.getRelationalEntity())
                .map(RelationalEntity::getVersionProperty)
                .flatMap(Function.identity())
                .ifPresent(relationalProperty -> updateColumnValues.put(relationalProperty.getColumn()
                        , relationalProperty.valueByObj(entity)));

        SqlAndParameterBind sqlAndParameterBind = updateValueBind(updateColumnValues);
        String updateIdWhere = buildIdBind(entity, sqlAndParameterBind.getParameterBind());
        return update(updateColumnValues, sqlAndParameterBind.getSql(), updateIdWhere, sqlAndParameterBind.getParameterBind());
    }

    @Override
    public <T> SqlAndParameterBind update(CriteriaUpdate<T, ?, ?> criteria) {
        Preconditions.checkArgument(criteria instanceof UpdateStructure, "不支持的查询结构类型.");
        UpdateStructure structure = (UpdateStructure) criteria;
        Map<String, Object> values = structure.getSetValues();
        SqlAndParameterBind sqlAndParameterBind = updateValueBind(values);
        SqlAndParameterBind whereBind = structureWhereBind(structure, sqlAndParameterBind.getParameterBind(), false);
        return update(values, sqlAndParameterBind.getSql(), whereBind.getSql(), sqlAndParameterBind.getParameterBind());
    }

    private SqlAndParameterBind updateValueBind(Map<String, Object> setValues) {
        entityMetadata.getRelationalEntity().getProperties().stream()
                .filter(relationalProperty -> relationalProperty.getValue().isUpdateFill())
                .forEach(relationalProperty -> setValues.put(relationalProperty.getColumn(), null));

        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind();
        StringJoinerBuilder columnValueJoin = StringJoinerBuilder.createSpaceJoin();
        for (String column : entityMetadata.getColumns()) {
            if (setValues.containsKey(column)) {
                RelationalProperty property = entityMetadata.getPropertyByColumnName(column);
                if (property.getValue().isUpdateable()) {
                    sqlAndParameterBind.bind(setValues.get(column), property.getColumn(), entityMetadata.getEntityClass());
                    columnValueJoin.withElements(property.getColumn(), EQUALS_TO, "?", ",");
                }
            }
        }
        return sqlAndParameterBind.setSql(columnValueJoin.build());
    }

    private SqlAndParameterBind update(Map<String, Object> setValues, String updateValue, String where, ParameterBind parameterBind) {
        if (entityMetadata.getSupports().isSupportVersion()) {
            Object version = setValues.get(entityMetadata.getVersionProperty().getColumn());
            if (version != null) {
                String versionWhere = buildVersionBind(version, parameterBind);
                where = where + SPACE + versionWhere;
            }
        }

        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind(where, parameterBind);
        fixedWhereCondition(sqlAndParameterBind, false);
        where = sqlAndParameterBind.getSql();

        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(UPDATE, TABLE_P, SET)
                .withElements(StringUtils.trim(updateValue, null, ","))
                .withElements(StringUtils.isNotBlank(where), WHERE)
                .withElements(StringUtils.trim(where, AND, null))
                .build();
        sql = sqlRender.render(sql);
        return new SqlAndParameterBind(parameterBind).setSql(sql);
    }

    @Override
    public <ID> SqlAndParameterBind deleteById(ID id) {
        return byIdBind(id, this::getDeleteSql);
    }

    @Override
    public <ID> SqlAndParameterBind deleteByIds(Collection<ID> ids) {
        return byIdsBind(ids, this::getDeleteSql);
    }

    @Override
    public <T> SqlAndParameterBind delete(CriteriaDelete<T, ?, ?> criteria) {
        return delete(criteria, this::getDeleteSql);
    }

    @Override
    public <ID> SqlAndParameterBind logicDeleteById(ID id) {
        return byIdBind(id, this::getLogicDeleteSql);
    }

    @Override
    public <ID> SqlAndParameterBind logicDeleteByIds(Collection<ID> ids) {
        return byIdsBind(ids, this::getLogicDeleteSql);
    }

    @Override
    public <T> SqlAndParameterBind logicDelete(CriteriaDelete<T, ?, ?> criteria) {
        return delete(criteria, this::getLogicDeleteSql);
    }

    private <T> SqlAndParameterBind delete(CriteriaDelete<T, ?, ?> criteria
            , Function<SqlAndParameterBind, SqlAndParameterBind> deleteSqlHandler) {
        Preconditions.checkArgument(criteria instanceof WhereStructure, "不支持的查询结构类型.");
        WhereStructure structure = (WhereStructure) criteria;
        SqlAndParameterBind sqlAndParameterBind = structureWhereBind(structure, null, true);
        return deleteSqlHandler.apply(sqlAndParameterBind);
    }

    private SqlAndParameterBind getDeleteSql(SqlAndParameterBind sqlAndParameterBind) {
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(DELETE)
                .withElements(FROM_TABLE_P)
                .withElements(WHERE)
                .withElements(StringUtils.trim(sqlAndParameterBind.getSql(), AND, null))
                .build();
        sql = sqlRender.render(sql);
        return sqlAndParameterBind.setSql(sql);
    }

    private SqlAndParameterBind getLogicDeleteSql(SqlAndParameterBind sqlAndParameterBind) {
        StringJoinerBuilder stringJoinerBuilder = StringJoinerBuilder.createSpaceJoin()
                .withElements(UPDATE)
                .withElements(TABLE_P, SET);

        SqlAndParameterBind setSqlAndParameterBind = new SqlAndParameterBind();
        StringJoinerBuilder setStringJoinerBuilder = StringJoinerBuilder.createSpaceJoin();
        for (RelationalProperty property : entityMetadata.getRelationalEntity().getProperties()) {
            if (property.getValue().isLogicDelete()) {
                setSqlAndParameterBind.bind(null, property.getColumn(), entityMetadata.getEntityClass());
                setStringJoinerBuilder.withElements(property.getColumn(), EQUALS_TO, "?", ",");
            }
        }

        String sql = stringJoinerBuilder
                .withElements(StringUtils.trim(setStringJoinerBuilder.build(), null, ","))
                .withElements(WHERE)
                .withElements(StringUtils.trim(sqlAndParameterBind.getSql(), AND, null))
                .build();

        sqlAndParameterBind.getParameterBind().getParameterColumnBinds()
                .forEach(parameterColumnBind -> setSqlAndParameterBind.bindConditionValue(parameterColumnBind.getValue()
                        , parameterColumnBind.getColumn(), parameterColumnBind.getEntityClass()));

        sql = sqlRender.render(sql);
        return setSqlAndParameterBind.setSql(sql);
    }

    @Override
    public SqlAndParameterBind provideJpaFind(boolean distinct, String whereSql, String orderBy, Integer limit) {
        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind(whereSql);
        fixedWhereCondition(sqlAndParameterBind, true);
        String sql = getSimpleSelectSql(distinct, sqlAndParameterBind.getSql());
        if (StringUtils.isNotBlank(orderBy)) {
            sql = sql + SPACE + sqlRender.render(orderBy);
        }
        if (limit > 0) {
            sql = dialect.getLimitHandler().applyLimit(sql, 0, limit);
        }
        return sqlAndParameterBind.setSql(sql);
    }

    @Override
    public SqlAndParameterBind provideJpaCount(String where) {
        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind(where);
        fixedWhereCondition(sqlAndParameterBind, true);
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(SELECT)
                .withElements("COUNT(*)")
                .withElements(FROM_TABLE_P)
                .withElements(WHERE)
                .withElements(StringUtils.trim(sqlAndParameterBind.getSql(), AND, null))
                .build();
        sql = sqlRender.render(sql);
        return sqlAndParameterBind.setSql(sql);
    }

    @Override
    public SqlAndParameterBind provideJpaExists(String where) {
        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind(where);
        fixedWhereCondition(sqlAndParameterBind, true);
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(SELECT)
                .withElements("1")
                .withElements(FROM_TABLE_P)
                .withElements(WHERE)
                .withElements(StringUtils.trim(sqlAndParameterBind.getSql(), AND, null))
                .build();
        sql = sqlRender.render(sql);
        sql = dialect.getLimitHandler().applyLimit(sql, 0, 1);
        return sqlAndParameterBind.setSql(sql);
    }

    @Override
    public SqlAndParameterBind provideJpaDelete(String where) {
        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind(where);
        fixedWhereCondition(sqlAndParameterBind, true);
        return getDeleteSql(sqlAndParameterBind);
    }

    @Override
    public SqlAndParameterBind provideJpaLogicDelete(String where) {
        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind(where);
        fixedWhereCondition(sqlAndParameterBind, true);
        return getLiteralLogicDeleteSql(sqlAndParameterBind);
    }

    private SqlAndParameterBind getLiteralLogicDeleteSql(SqlAndParameterBind where) {
        RelationalProperty logicDeleteProperty = entityMetadata.getLogicDeleteProperty();
        String logicDeletePlaceholder = logicDeleteProperty.getColumn() + "_logicDeleteValue";
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(UPDATE)
                .withElements(TABLE_P, SET)
                .withElements(logicDeleteProperty.getColumn()
                        , EQUALS_TO, SQLPart.placeholder(logicDeletePlaceholder))
                .withElements(WHERE)
                .withElements(StringUtils.trim(where.getSql(), AND, null))
                .build();

        where.addParameterHandle((o, parameterColumnBinds, boundSql) -> {
            boundSql.setAdditionalParameter(logicDeletePlaceholder
                    , logicDeleteProperty.getAsLogicDeleteValue().logicDeleteHandler().setValue(null));
            return null;
        });

        where.setSql(sqlRender.render(sql));
        return where;
    }

    private <ID> SqlAndParameterBind byIdBind(ID id, Function<SqlAndParameterBind, SqlAndParameterBind> sqlHandle) {
        String idColumn = entityMetadata.getPrimaryKeyProperty().getColumn();
        ColumnCriterion<ID> criterion = new EqualTo<>(idColumn, id);
        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind(criterion.getSql());
        sqlAndParameterBind.bindConditionValue(id, idColumn, entityMetadata.getEntityClass());
        if (id == SqlSourceScriptBuilder.PLACEHOLDER_OBJECT) {
            sqlAndParameterBind.addParameterHandle((idValue, parameterColumnBinds, boundSql) -> {
                parameterColumnBinds.forEach(parameterColumnBind -> {
                    if (parameterColumnBind.getColumn().equals(idColumn)) {
                        boundSql.setAdditionalParameter(parameterColumnBind.getParameter(), idValue);
                    } else {
                        boundSql.setAdditionalParameter(parameterColumnBind.getParameter(), null);
                    }
                });
                return null;
            });
        }
        return sqlHandle.apply(fixedWhereCondition(sqlAndParameterBind, false));
    }

    private <ID> SqlAndParameterBind byIdsBind(Collection<ID> ids, Function<SqlAndParameterBind, SqlAndParameterBind> sqlHandle) {
        ColumnCriterion<Collection<ID>> criterion = new In<>(entityMetadata.getPrimaryKeyProperty().getColumn(), ids);
        SqlAndParameterBind sqlAndParameterBind = criterion.accept(new PreparedSQLVisitor(entityMetadata.getEntityClass()));
        return sqlHandle.apply(fixedWhereCondition(sqlAndParameterBind, false));
    }

    private <T> String buildIdBind(T entity, ParameterBind parameterBind) {
        RelationalProperty idProperty = entityMetadata.getPrimaryKeyProperty();
        Object id = idProperty.valueByObj(entity);
        return buildEqualBind(id, idProperty.getColumn(), parameterBind);
    }

    private <Version> String buildVersionBind(Version version, ParameterBind parameterBind) {
        return buildEqualBind(version, entityMetadata.getVersionProperty().getColumn(), parameterBind);
    }

    private String buildEqualBind(Object value, String column, ParameterBind parameterBind) {
        ColumnCriterion<?> criterion = new EqualTo<>(safeColum(column), value);
        parameterBind.bindConditionValue(value, column, entityMetadata.getEntityClass());
        return criterion.getSql();
    }

    private SqlAndParameterBind structureWhereBind(WhereStructure structure, ParameterBind parameterBind, boolean turnOnLogicDelete) {
        parameterBind = parameterBind == null ? new ParameterBind() : parameterBind;
        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind(parameterBind);
        String whereSql = EMPTY;
        if (structure instanceof QueryStructure) {
            QueryStructure queryStructure = (QueryStructure) structure;
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
        return fixedWhereCondition(sqlAndParameterBind, false, turnOnLogicDelete, structure.isIncludeLogicDelete());
    }

    private SqlAndParameterBind fixedWhereCondition(SqlAndParameterBind sqlAndParameterBind, boolean isPlaceholder) {
        return fixedWhereCondition(sqlAndParameterBind, isPlaceholder, true, false);
    }

    private SqlAndParameterBind fixedWhereCondition(SqlAndParameterBind sqlAndParameterBind, boolean isPlaceholder
            , boolean turnOnLogicDelete, boolean includeLogicDelete) {
        entityMetadata.getRelationalEntity().getProperties().stream()
                .filter(relationalProperty -> relationalProperty.getValue().isConditionFill())
                .forEach(relationalProperty -> {
                    ColumnCriterion<?> criterion = new EqualTo<>(safeColum(relationalProperty.getColumn()), null);
                    sqlAndParameterBind.bindConditionValue(null, relationalProperty.getColumn(), entityMetadata.getEntityClass());
                    String sql = sqlAndParameterBind.getSql() == null
                            ? criterion.getSql() : sqlAndParameterBind.getSql() + SPACE + criterion.getSql();
                    sqlAndParameterBind.setSql(sql);
                });

        if (turnOnLogicDelete && entityMetadata.getSupports().isSupportLogicDelete() && !includeLogicDelete) {
            RelationalProperty deleteProperty = entityMetadata.getLogicDeleteProperty();
            Serializable defaultValue = deleteProperty.getValue().getDefaultValue();
            ColumnCriterion<?> criterion;
            if (defaultValue == null) {
                criterion = new IsNull(safeColum(deleteProperty.getColumn()));
            } else {
                criterion = new EqualTo<>(safeColum(deleteProperty.getColumn()), defaultValue);
                if (isPlaceholder) {
                    String defaultValuePlaceholder = deleteProperty.getColumn() + "_logicDeleteDefaultValue";
                    criterion.setSqlStrategy((SQLStrategy) () -> criterion.patternSql(SQLPart.placeholder(defaultValuePlaceholder)));
                    sqlAndParameterBind.addParameterHandle((o, parameterColumnBinds, boundSql) -> {
                        boundSql.setAdditionalParameter(defaultValuePlaceholder, defaultValue);
                        return null;
                    });
                } else {
                    sqlAndParameterBind.bindConditionValue(defaultValue, deleteProperty.getColumn(), entityMetadata.getEntityClass());
                }
            }
            String sql = sqlAndParameterBind.getSql() == null
                    ? criterion.getSql() : sqlAndParameterBind.getSql() + SPACE + criterion.getSql();
            sqlAndParameterBind.setSql(sql);
        }
        return sqlAndParameterBind;
    }

    private <T> Map<String, Object> getEntityColumnValues(T entity, boolean nullable) {
        Map<String, Object> columnValues = new LinkedHashMap<>(entityMetadata.getRelationalEntity().getProperties().size());
        for (RelationalProperty property : entityMetadata.getRelationalEntity().getProperties()) {
            Object value = property.valueByObj(entity);
            if (value != null || !nullable) {
                columnValues.put(property.getColumn(), value);
            }
        }
        return columnValues;
    }


    private String selectResult() {
        StringBuilder sqlBuilder = new StringBuilder();
        for (RelationalProperty property : entityMetadata.getRelationalEntity().getProperties()) {
            String safeColumn = safeColum(property.getColumn());
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

    private String safeColum(String column) {
        return SQLPart.safeColumnName(column, dialect);
    }

}
