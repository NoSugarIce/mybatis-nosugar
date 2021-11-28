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

import com.nosugarice.mybatis.builder.EntityMetadata;
import com.nosugarice.mybatis.dialect.Dialect;
import com.nosugarice.mybatis.domain.Page;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.query.criteria.EntityCriteriaQuery;
import com.nosugarice.mybatis.query.criteria.QueryStructure;
import com.nosugarice.mybatis.query.criterion.ColumnCriterion;
import com.nosugarice.mybatis.query.criterion.Equal;
import com.nosugarice.mybatis.query.criterion.In;
import com.nosugarice.mybatis.sql.ParameterBind.ParameterColumnBind;
import com.nosugarice.mybatis.sql.render.CriteriaQuerySQLRender;
import com.nosugarice.mybatis.sql.render.EntitySQLRender;
import com.nosugarice.mybatis.sql.render.PreparedVisitor;
import com.nosugarice.mybatis.util.Preconditions;
import com.nosugarice.mybatis.util.StringJoinerBuilder;
import com.nosugarice.mybatis.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
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
 * @date 2021/9/25
 */
public class ProviderTempLateImpl implements ProviderTempLate {

    private final EntityMetadata entityMetadata;
    private final Dialect dialect;
    private final EntitySqlPart entitySqlPart;
    private final EntitySQLRender sqlRender;

    public ProviderTempLateImpl(EntityMetadata entityMetadata, Dialect dialect) {
        this.entityMetadata = entityMetadata;
        this.dialect = dialect;
        this.entitySqlPart = new EntitySqlPart(entityMetadata, dialect);
        this.sqlRender = new EntitySQLRender.Builder()
                .withTable(entityMetadata.getRelationalEntity().getTable().getName())
                .withSupportDynamicTableName(entityMetadata.getSupports().isSupportDynamicTableName())
                .build();
    }

    @Override
    public <ID> SqlAndParameterBind selectById(ID id) {
        return byIdBind(id, this::getSelectByIdSql);
    }

    @Override
    public <ID> SqlAndParameterBind selectByIds(Collection<ID> ids) {
        return byIdsBind(ids, this::getSelectByIdSql);
    }

    private String getSelectByIdSql(String where) {
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(SELECT)
                .withElements(StringUtils.trim(entitySqlPart.selectResult, null, ","))
                .withElements(FROM_TABLE_P)
                .withElements(WHERE)
                .withElements(StringUtils.trim(where, AND, null))
                .build();
        sql = sqlRender.renderWithTableAlias(sql, false);
        return sql;
    }

    @Override
    public <T> SqlAndParameterBind selectList(EntityCriteriaQuery<T> criteria) {
        return selectListLimit(criteria, null);
    }

    @Override
    public <T> SqlAndParameterBind selectListLimit(EntityCriteriaQuery<T> criteria, Page<T> page) {
        SqlAndParameterBind sqlAndParameterBind = entityCriteriaQueryBind(criteria, null);
        String sql;
        String whereSql = buildWhereSql(sqlAndParameterBind.getSql());
        if (criteria.isSimple()) {
            sql = StringJoinerBuilder.createSpaceJoin()
                    .withElements(SELECT)
                    .withElements(StringUtils.trim(entitySqlPart.selectResult, null, ","))
                    .withElements(FROM_TABLE_P)
                    .withElements(StringUtils.isNotBlank(whereSql), WHERE)
                    .withElements(StringUtils.trim(whereSql, AND, null))
                    .build();
            sql = sqlRender.renderWithTableAlias(sql, false);
        } else {
            Preconditions.checkArgument(criteria instanceof QueryStructure, "不支持的查询结构类型.");
            QueryStructure<?> queryStructure = (QueryStructure<?>) criteria;
            CriteriaQuerySQLRender render = queryStructure.getRender(sqlRender);

            String result = StringJoinerBuilder.createSpaceJoin()
                    .withElements(queryStructure.getColumnSelections().isPresent() ? render.renderColumnSelect() : entitySqlPart.selectResult)
                    .withElements(queryStructure.getFunctionSelections().isPresent(), render.renderFunctionSelect())
                    .withElements(queryStructure.getJoinCriteria().isPresent(), render.renderJoinSelect())
                    .build();
            sql = StringJoinerBuilder.createSpaceJoin()
                    .withElements(SELECT)
                    .withElements(queryStructure.isDistinct(), DISTINCT)
                    .withElements(StringUtils.trim(result, null, ","))
                    .withElements(render.renderFrom())
                    .withElements(queryStructure.getJoinCriteria().isPresent(), render.renderJoinFom())
                    .withElements(StringUtils.isNotBlank(whereSql), WHERE)
                    .withElements(StringUtils.trim(whereSql, AND, null))
                    .withElements(render.renderGroupBy())
                    .withElements(render.renderHaving())
                    .withElements(render.renderOrderBy())
                    .build();
            sql = sqlRender.renderWithTableAlias(sql, true);
        }
        if (page != null) {
            sql = dialect.getLimitHandler().processSql(sql, page.getOffset(), page.getLimit());
        }
        sqlAndParameterBind.setSql(sql);
        return sqlAndParameterBind;
    }

    @Override
    public <T> SqlAndParameterBind insert(T entity) {
        StringJoinerBuilder columnJoin = StringJoinerBuilder.createSpaceJoin().withDelimiter(", ").withPrefix("(").withSuffix(")");
        StringJoinerBuilder valueJoin = StringJoinerBuilder.createSpaceJoin().withDelimiter(", ").withPrefix("(").withSuffix(")");

        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind();
        for (RelationalProperty property : entityMetadata.getRelationalEntity().getProperties()) {
            if (!property.getValue().isInsertable()) {
                continue;
            }
            sqlAndParameterBind.bind(null, property.getColumn(), entityMetadata.getEntityClass()).canHandle();
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
        sqlAndParameterBind.setParameterHandle((t, parameterColumnBinds, boundSql) -> {
            Map<String, Object> columnValues = getEntityColumnValues(t);
            for (ParameterColumnBind parameterColumnBind : parameterColumnBinds) {
                boundSql.setAdditionalParameter(parameterColumnBind.getParameter(), columnValues.get(parameterColumnBind.getColumn()));
            }
            return null;
        });
        return sqlAndParameterBind;
    }

    @Override
    public <T> SqlAndParameterBind insertNullable(T entity) {
        StringJoinerBuilder columnJoin = StringJoinerBuilder.createSpaceJoin().withDelimiter(", ").withPrefix("(").withSuffix(")");
        StringJoinerBuilder valueJoin = StringJoinerBuilder.createSpaceJoin().withDelimiter(", ").withPrefix("(").withSuffix(")");

        Map<String, Object> columnValues = getEntityColumnValues(entity);
        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind();
        for (RelationalProperty property : entityMetadata.getRelationalEntity().getProperties()) {
            if (!property.getValue().isInsertable()) {
                continue;
            }
            Object value = columnValues.get(property.getColumn());
            if (value == null) {
                if (property.isNullable()) {
                    continue;
                }
                throw new IllegalArgumentException();
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
    public <T> SqlAndParameterBind updateById(T entity) {
        return updateById(entity, entityMetadata.getRelationalEntity().getProperties(), false);
    }

    @Override
    public <T> SqlAndParameterBind updateByIdChoseProperty(T entity, Set<String> choseProperties) {
        List<RelationalProperty> relationalProperties = entityMetadata.getRelationalEntity().getProperties().stream()
                .filter(relationalProperty -> choseProperties.contains(relationalProperty.getName()))
                .collect(Collectors.toList());
        return updateById(entity, relationalProperties, false);
    }

    @Override
    public <T> SqlAndParameterBind updateByIdNullable(T entity) {
        return updateById(entity, entityMetadata.getRelationalEntity().getProperties(), true);
    }

    private <T> SqlAndParameterBind updateById(T entity, List<RelationalProperty> relationalProperties, boolean nullable) {
        SqlAndParameterBind sqlAndParameterBind = updateValue(entity, relationalProperties, nullable);
        String updateIdWhere = buildIdBind(entity, sqlAndParameterBind.getParameterBind());
        return update(entity, sqlAndParameterBind.getSql(), updateIdWhere, sqlAndParameterBind.getParameterBind());
    }

    @Override
    public <T> SqlAndParameterBind update(T entity, EntityCriteriaQuery<T> criteria) {
        return updateByCriteria(entity, criteria, false);
    }

    @Override
    public <T> SqlAndParameterBind updateNullable(T entity, EntityCriteriaQuery<T> criteria) {
        return updateByCriteria(entity, criteria, true);
    }

    private <T> SqlAndParameterBind updateByCriteria(T entity, EntityCriteriaQuery<T> criteria, boolean nullable) {
        SqlAndParameterBind sqlAndParameterBind = updateValue(entity, entityMetadata.getRelationalEntity().getProperties(), nullable);
        String updateCriteriaWhere = entityCriteriaQueryBind(criteria, sqlAndParameterBind.getParameterBind()).getSql();
        return update(entity, sqlAndParameterBind.getSql(), updateCriteriaWhere, sqlAndParameterBind.getParameterBind());
    }

    private <T> SqlAndParameterBind updateValue(T entity, List<RelationalProperty> relationalProperties, boolean nullable) {
        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind();
        Map<String, Object> columnValues = getEntityColumnValues(entity);
        StringJoinerBuilder columnValueJoin = StringJoinerBuilder.createSpaceJoin();
        for (RelationalProperty property : relationalProperties) {
            if (!property.getValue().isUpdateable()) {
                continue;
            }
            Object value = columnValues.get(property.getColumn());
            if (value == null && nullable) {
                continue;
            }
            sqlAndParameterBind.bind(value, property.getColumn(), entityMetadata.getEntityClass()).canHandle();
            columnValueJoin.withElements(property.getColumn(), EQUALS_TO, "?", ",");
        }
        sqlAndParameterBind.setSql(columnValueJoin.build());
        return sqlAndParameterBind;
    }

    private <T> SqlAndParameterBind update(T entity, String updateValue, String where, ParameterBind parameterBind) {
        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind(parameterBind);
        if (entityMetadata.getSupports().isSupportVersion()) {
            Object version = entityMetadata.getVersionProperty().getValue(entity);
            if (version != null) {
                String versionWhere = buildVersionBind(version, sqlAndParameterBind.getParameterBind());
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
        sqlAndParameterBind.setSql(sql);
        return sqlAndParameterBind;
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
    public <T> SqlAndParameterBind delete(EntityCriteriaQuery<T> criteria) {
        SqlAndParameterBind sqlAndParameterBind = entityCriteriaQueryBind(criteria, null);
        sqlAndParameterBind.setSql(getDeleteSql(sqlAndParameterBind.getSql()));
        return sqlAndParameterBind;
    }

    private String getDeleteSql(String where) {
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(DELETE)
                .withElements(FROM_TABLE_P)
                .withElements(WHERE)
                .withElements(StringUtils.trim(where, AND, null))
                .build();
        sql = sqlRender.renderWithTableAlias(sql, false);
        return sql;
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
    public <T> SqlAndParameterBind logicDelete(EntityCriteriaQuery<T> criteria) {
        SqlAndParameterBind sqlAndParameterBind = entityCriteriaQueryBind(criteria, null);
        sqlAndParameterBind.setSql(getLogicDeleteSql(sqlAndParameterBind.getSql()));
        return sqlAndParameterBind;
    }

    private String getLogicDeleteSql(String where) {
        RelationalProperty logicDeleteProperty = entityMetadata.getLogicDeleteProperty();
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(UPDATE)
                .withElements(FROM_TABLE_P, SET)
                .withElements(logicDeleteProperty.getColumn()
                        , EQUALS_TO, literalValue(logicDeleteProperty.getAsLogicDeleteValue().getLogicDeleteValue()))
                .withElements(WHERE)
                .withElements(StringUtils.trim(where, AND, null))
                .build();
        sql = sqlRender.renderWithTableAlias(sql, false);
        return sql;
    }

    @Override
    public SqlAndParameterBind provideJpaFind(String whereSql, String orderBy, Integer limit) {
        String sql = getSelectByIdSql(whereSql);
        if (StringUtils.isNotBlank(orderBy)) {
            sql = sql + SPACE + sqlRender.renderWithTableAlias(orderBy, false);
        }
        if (limit > 0) {
            sql = dialect.getLimitHandler().processSql(sql, 0, limit);
        }
        return new SqlAndParameterBind(sql);
    }

    @Override
    public SqlAndParameterBind provideJpaCount(String whereSql) {
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(SELECT)
                .withElements("COUNT(*)")
                .withElements(FROM_TABLE_P)
                .withElements(WHERE)
                .withElements(StringUtils.trim(whereSql, AND, null))
                .build();
        sql = sqlRender.renderWithTableAlias(sql, false);
        return new SqlAndParameterBind(sql);
    }

    @Override
    public SqlAndParameterBind provideJpaExists(String whereSql) {
        String sql = StringJoinerBuilder.createSpaceJoin()
                .withElements(SELECT)
                .withElements("1")
                .withElements(FROM_TABLE_P)
                .withElements(WHERE)
                .withElements(StringUtils.trim(whereSql, AND, null))
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
        String sql = sqlHandle.apply(buildWhereSql(sqlAndParameterBind.getSql()));
        sqlAndParameterBind.setSql(sql);
        return sqlAndParameterBind;
    }

    private <ID> SqlAndParameterBind byIdsBind(Collection<ID> ids, Function<String, String> sqlHandle) {
        ColumnCriterion<ID> criterion = new In<>(entityMetadata.getPrimaryKeyProperty().getColumn(), ids);
        SqlAndParameterBind sqlAndParameterBind = new PreparedVisitor(entityMetadata.getEntityClass()).visit(criterion);
        String sql = sqlHandle.apply(buildWhereSql(sqlAndParameterBind.getSql()));
        sqlAndParameterBind.setSql(sql);
        return sqlAndParameterBind;
    }

    private <ID> SqlAndParameterBind getByIdBind(ID id) {
        String idColumn = entityMetadata.getPrimaryKeyProperty().getColumn();
        ColumnCriterion<ID> criterion = new Equal<>(idColumn, id);
        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind(criterion.getSql());
        sqlAndParameterBind.bind(id, idColumn, entityMetadata.getEntityClass());
        sqlAndParameterBind.setParameterHandle((idValue, parameterColumnBinds, boundSql) -> {
            parameterColumnBinds.forEach(parameterColumnBind -> boundSql.setAdditionalParameter(parameterColumnBind.getParameter(), idValue));
            return null;
        });
        return sqlAndParameterBind;
    }

    private <T> String buildIdBind(T entity, ParameterBind parameterBind) {
        RelationalProperty idProperty = entityMetadata.getPrimaryKeyProperty();
        Object id = idProperty.getValue(entity);
        return buildEqualBind(id, idProperty.getColumn(), parameterBind);
    }

    private <Version> String buildVersionBind(Version version, ParameterBind parameterBind) {
        RelationalProperty versionProperty = entityMetadata.getVersionProperty();
        return buildEqualBind(version, versionProperty.getColumn(), parameterBind);
    }

    private String buildEqualBind(Object value, String column, ParameterBind parameterBind) {
        ColumnCriterion<?> criterion = new Equal<>(column, value);
        parameterBind.bindValue(value, column, entityMetadata.getEntityClass());
        return criterion.getSql();
    }

    private String buildWhereSql(String where) {
        return entityMetadata.getSupports().isSupportLogicDelete() ? where + SPACE + entitySqlPart.selectParameterLogicDelete : where;
    }

    private <T> SqlAndParameterBind entityCriteriaQueryBind(EntityCriteriaQuery<T> criteria, ParameterBind parameterBind) {
        parameterBind = parameterBind == null ? new ParameterBind() : parameterBind;
        SqlAndParameterBind sqlAndParameterBind = new SqlAndParameterBind(parameterBind);
        PreparedVisitor visitor = new PreparedVisitor(entityMetadata.getEntityClass(), parameterBind);
        String whereSql = EMPTY;
        if (criteria.getEntity() != null) {
            Map<String, Object> columnValues = getEntityColumnValues(criteria.getEntity());
            List<ColumnCriterion<?>> columnCriteria = new ArrayList<>(columnValues.size());
            for (Map.Entry<String, Object> entry : columnValues.entrySet()) {
                if (entry.getValue() != null) {
                    ColumnCriterion<?> criterion = new Equal<>(entry.getKey(), entry.getValue());
                    columnCriteria.add(criterion);
                }
            }
            whereSql = CriterionSqlUtils.<SqlAndParameterBind>criterionsToSqlFunction()
                    .apply(columnCriteria, visitor, SqlAndParameterBind::getSql);
        }
        if (criteria instanceof QueryStructure) {
            QueryStructure<?> queryStructure = (QueryStructure<?>) criteria;
            CriteriaQuerySQLRender render = queryStructure.getRender(sqlRender);
            boolean hasCriterion = queryStructure.hasCriterion();
            if (hasCriterion) {
                whereSql = whereSql + SPACE + render.renderWhere(parameterBind);
            }
            if (queryStructure.getJoinCriteria().isPresent()) {
                whereSql = whereSql + SPACE + render.renderJoinWhere(parameterBind);
            }
        }
        sqlAndParameterBind.setSql(whereSql);
        return sqlAndParameterBind;
    }


    private <T> Map<String, Object> getEntityColumnValues(T entity) {
        Map<String, Object> columnValues = new LinkedHashMap<>(entityMetadata.getRelationalEntity().getProperties().size());
        for (RelationalProperty property : entityMetadata.getRelationalEntity().getProperties()) {
            columnValues.put(property.getColumn(), property.getValue(entity));
        }
        return columnValues;
    }

    private String literalValue(Serializable value) {
        return dialect.getLiteralValueHandler().convert(value);
    }

}
