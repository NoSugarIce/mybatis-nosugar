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

package com.nosugarice.mybatis.builder.mapper;

import com.nosugarice.mybatis.builder.SqlSourceScriptBuilder;
import com.nosugarice.mybatis.builder.statement.StatementBuilder;
import com.nosugarice.mybatis.criteria.select.OrderByCriterion;
import com.nosugarice.mybatis.criteria.where.ColumnCriterion;
import com.nosugarice.mybatis.criteria.where.CriterionSQLVisitor;
import com.nosugarice.mybatis.criteria.where.GroupCriterion;
import com.nosugarice.mybatis.criteria.where.criterion.GroupCriterionImpl;
import com.nosugarice.mybatis.criteria.where.criterion.Like;
import com.nosugarice.mybatis.domain.Page;
import com.nosugarice.mybatis.jpa.ConditionType;
import com.nosugarice.mybatis.jpa.parser.OrderBySource;
import com.nosugarice.mybatis.jpa.parser.Part;
import com.nosugarice.mybatis.jpa.parser.PartTree;
import com.nosugarice.mybatis.jpa.parser.PartTree.Predicate;
import com.nosugarice.mybatis.jpa.parser.Subject;
import com.nosugarice.mybatis.mapper.function.FunS;
import com.nosugarice.mybatis.mapper.function.JpaMapper;
import com.nosugarice.mybatis.mapping.RelationalEntity;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.sql.ProviderTempLate;
import com.nosugarice.mybatis.sql.SQLPart;
import com.nosugarice.mybatis.sql.SqlAndParameterBind;
import com.nosugarice.mybatis.sql.SqlBuilder;
import com.nosugarice.mybatis.sql.render.AbstractRenderingContext;
import com.nosugarice.mybatis.util.Preconditions;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.StringTypeHandler;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.nosugarice.mybatis.sql.SQLConstants.AND;
import static com.nosugarice.mybatis.sql.SQLConstants.EMPTY;
import static com.nosugarice.mybatis.sql.SQLConstants.SPACE;

/**
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 * 简单实现
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/12/5
 */
public class JpaMapperBuilder extends AbstractMapperBuilder<JpaMapperBuilder> {

    private RelationalEntity relationalEntity;
    private StatementBuilder statementBuilder;

    private static final Map<Class<? extends ColumnCriterion<?>>, Class<? extends TypeHandler<?>>> CRITERION_TYPE_HANDLER_MAP
            = new HashMap<>();

    static {
        CRITERION_TYPE_HANDLER_MAP.put(Like.StartLike.class, StartLikeTypeHandler.class);
        CRITERION_TYPE_HANDLER_MAP.put(Like.EndLike.class, EenLikeTypeHandler.class);
        CRITERION_TYPE_HANDLER_MAP.put(Like.AnyLike.class, AnyLikeTypeHandler.class);
    }

    @Override
    public JpaMapperBuilder build() {
        super.build();
        this.relationalEntity = entityMetadata.getRelationalEntity();
        this.statementBuilder = StatementBuilder.of(JpaMapper.class)
                .withMapper(mapperClass).withBuildingContext(buildingContext).build();
        return this;
    }

    @Override
    public boolean isMapper() {
        return JpaMapper.class.isAssignableFrom(mapperClass);
    }

    @Override
    public boolean isCrudMethod(Method method) {
        return notHasStatement(method) && PartTree.PREFIX_TEMPLATE.matcher(method.getName()).find()
                && !method.isAnnotationPresent(SqlBuilder.class);
    }

    @Override
    public void checkBeforeProcessMethod(Method method) {
    }

    @Override
    public void processMethod(Method method) {
        String methodName = method.getName();
        Matcher matcher = PartTree.PREFIX_TEMPLATE.matcher(methodName);
        if (matcher.find()) {
            PartTree partTree = new PartTree(methodName);
            Predicate predicate = partTree.getPredicate();
            List<Part> parts = new ArrayList<>();
            predicate.getNodes().forEach(orPart -> parts.addAll(orPart.getChildren()));
            parts.forEach(part -> Preconditions.checkArgument(entityMetadata.existProperty(part.getPropertyName())
                    , String.format("No property %s found for type %s!", part.getPropertyName()
                            , relationalEntity.getName())));
            int totalNumberOfParameters = parts.stream().mapToInt(Part::getNumberOfArguments).sum();
            long parameterCount = Arrays.stream(method.getParameterTypes())
                    .filter(clazz -> !Page.class.isAssignableFrom(clazz))
                    .filter(clazz -> !RowBounds.class.isAssignableFrom(clazz))
                    .count();
            Preconditions.checkArgument(totalNumberOfParameters == parameterCount
                    , String.format("Method %s expects at least %d arguments but only found %d."
                            , methodName, totalNumberOfParameters, parameterCount));
            predicate.getOrderBySource().map(OrderBySource::getOrderByCriterion).map(OrderByCriterion::getOrderByColumns)
                    .ifPresent(orderMap -> orderMap.keySet().forEach(propertyName ->
                            Preconditions.checkArgument(entityMetadata.existProperty(propertyName)
                                    , String.format("No property %s found for type %s!", propertyName, relationalEntity.getName()))));

            List<PartTree.OrPart> nodes = predicate.getNodes();
            List<GroupCriterion> groupCriteria = createGroupCriteria(nodes);
            String whereSql = getWhereSql(method, new GroupCriterionImpl().append(groupCriteria));

            SqlSourceScriptBuilder sqlSourceScriptBuilder = statementBuilder.getSqlScriptBuilder();
            Subject subject = partTree.getSubject();
            SqlAndParameterBind sqlAndParameterBind = null;
            FunS.Param2<ProviderTempLate, String, SqlAndParameterBind> providerFun = null;
            if (subject.isDelete()) {
                providerFun = ProviderTempLate::provideJpaDelete;
            } else if (subject.isLogicDelete()) {
                providerFun = ProviderTempLate::provideJpaLogicDelete;
            } else if (subject.isCount()) {
                providerFun = ProviderTempLate::provideJpaCount;
            } else if (subject.isExists()) {
                providerFun = ProviderTempLate::provideJpaExists;
            } else {
                FunS.Param4<ProviderTempLate, String, String, Integer, SqlAndParameterBind> providerFunParam4 = ProviderTempLate::provideJpaFind;
                Integer limit = subject.isLimiting() ? subject.getMaxResults() : 0;
                String orderBy = predicate.getOrderBySource().map(OrderBySource::getOrderByCriterion).map(OrderByCriterion::getSql).orElse(EMPTY);
                sqlAndParameterBind = sqlSourceScriptBuilder.build(providerFunParam4, whereSql, orderBy, limit);
            }
            if (sqlAndParameterBind == null) {
                Preconditions.checkNotNull(providerFun, "未设置构建SQL方法.");
                sqlAndParameterBind = sqlSourceScriptBuilder.build(providerFun, whereSql);
            }
            statementBuilder.addMappedStatement(method, SQLPart.script(sqlAndParameterBind.getSql()));
        }
    }

    private String getWhereSql(Method method, GroupCriterion groupCriterion) {
        String[] parameterNames = new ParamNameResolver(configuration, method).getNames();
        Iterator<String> paramNameIterator = Stream.of(parameterNames).collect(Collectors.toList()).iterator();
        Map<String, Class<? extends TypeHandler<?>>> columnTypeHandlerMap = getColumnTypeHandler(groupCriterion);

        CriterionSQLVisitor<String> visitor = new JpaRenderPlaceholderSQLVisitor(entityMetadata.getEntityClass()
                , paramNameIterator, columnTypeHandlerMap);
        return groupCriterion.accept(visitor);
    }

    private List<GroupCriterion> createGroupCriteria(List<PartTree.OrPart> nodes) {
        List<GroupCriterion> groupCriteria = new ArrayList<>();
        for (PartTree.OrPart node : nodes) {
            GroupCriterion groupCriterion = new GroupCriterionImpl();
            groupCriterion.byOr();
            groupCriteria.add(groupCriterion);
            List<Part> children = node.getChildren();
            for (Part child : children) {
                RelationalProperty relationalProperty = entityMetadata.getPropertyByPropertyName(child.getPropertyName());
                ConditionType conditionType = child.getConditionType();
                groupCriterion.append(conditionType.createColumnCriterion(relationalProperty.getColumn()));
            }
        }
        if (!groupCriteria.isEmpty()) {
            groupCriteria.get(0).byAnd();
        }
        return groupCriteria;
    }

    private Map<String, Class<? extends TypeHandler<?>>> getColumnTypeHandler(GroupCriterion groupCriterion) {
        List<ColumnCriterion<?>> columnCriterionList = groupCriterion.getColumnCriterions();

        Map<String, Class<? extends TypeHandler<?>>> columnTypeHandlerMap = new HashMap<>(columnCriterionList.size());
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        for (ColumnCriterion<?> columnCriterion : columnCriterionList) {
            Class<?> javaType = entityMetadata.getPropertyByColumnName(columnCriterion.getColumn()).getJavaType();
            Class<? extends TypeHandler<?>> typeHandlerType = CRITERION_TYPE_HANDLER_MAP.get(columnCriterion.getClass());
            if (typeHandlerType != null) {
                columnTypeHandlerMap.put(columnCriterion.getColumn(), typeHandlerType);
                TypeHandler<?> typeHandler = typeHandlerRegistry.getMappingTypeHandler(typeHandlerType);
                if (typeHandler == null) {
                    typeHandler = typeHandlerRegistry.getInstance(javaType, typeHandlerType);
                    typeHandlerRegistry.register(typeHandler);
                }
            }
        }
        return columnTypeHandlerMap;
    }

    private static class JpaRenderPlaceholderSQLVisitor implements CriterionSQLVisitor<String> {

        private final AbstractRenderingContext renderingContext;

        private JpaRenderPlaceholderSQLVisitor(Class<?> entityClass, Iterator<String> paramNameIterator
                , Map<String, Class<? extends TypeHandler<?>>> columnTypeHandlerMap) {
            this.renderingContext = new JpaRenderingContext(entityClass, paramNameIterator, columnTypeHandlerMap);
        }

        @Override
        public String visit(ColumnCriterion<?> criterion) {
            return renderingContext.getCriterionExpression(criterion);
        }

        @Override
        public String visit(GroupCriterion criterion) {
            setSQLStrategy(criterion, this);
            return criterion.getSql();
        }

        @Override
        public String visitResultHandle(String result) {
            return result;
        }

        private static class JpaRenderingContext extends AbstractRenderingContext {

            /** 加上jdbcType=OTHER 为了不覆盖默认的处理类型 */
            private static final String CLASH_ASSIGN_JDBC_TYPE = "jdbcType=OTHER";

            private final Map<String, Class<? extends TypeHandler<?>>> columnTypeHandlerMap;

            public JpaRenderingContext(Class<?> entityClass, Iterator<String> paramNameIterator
                    , Map<String, Class<? extends TypeHandler<?>>> columnTypeHandlerMap) {
                super(entityClass);
                this.columnTypeHandlerMap = columnTypeHandlerMap;
                setParamNameIterator(paramNameIterator);
            }

            @Override
            public String getSingleValuePlaceholder(String column) {
                return getValuePlaceholder(column, false);
            }

            @Override
            public String getTwoValuePlaceholder(String column) {
                return getSingleValuePlaceholder(column) + SPACE + AND + SPACE + getSingleValuePlaceholder(column);
            }

            @Override
            public String getListValuePlaceholder(String column) {
                return getValuePlaceholder(column, true);
            }

            private String getValuePlaceholder(String column, boolean isListValue) {
                if (paramNameIterator.hasNext()) {
                    String paramName = paramNameIterator.next();
                    paramNameIterator.remove();
                    String assignJdbcType = null;
                    String assignTypeHandler = null;
                    Class<? extends TypeHandler<?>> typeHandlerType = columnTypeHandlerMap.get(column);
                    if (typeHandlerType != null) {
                        assignJdbcType = CLASH_ASSIGN_JDBC_TYPE;
                        assignTypeHandler = SQLPart.assignTypeHandler(typeHandlerType);
                    }
                    if (isListValue) {
                        return "<foreach collection=\"" + paramName + "\" item=\"item\" open=\"(\" close=\")\" separator=\",\">" +
                                getPlaceholder(column, "item", null, assignJdbcType, assignTypeHandler) +
                                "</foreach>";
                    }
                    return getPlaceholder(column, paramName, null, assignJdbcType, assignTypeHandler);
                }
                return EMPTY;
            }
        }
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @MappedJdbcTypes(value = JdbcType.OTHER)
    public static class StartLikeTypeHandler extends StringTypeHandler {
        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
            super.setNonNullParameter(ps, i, Like.StartLike.MATCH.apply(parameter), jdbcType);
        }
    }

    @MappedJdbcTypes(value = JdbcType.OTHER)
    public static class EenLikeTypeHandler extends StringTypeHandler {
        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
            super.setNonNullParameter(ps, i, Like.EndLike.MATCH.apply(parameter), jdbcType);
        }
    }

    @MappedJdbcTypes(value = JdbcType.OTHER)
    public static class AnyLikeTypeHandler extends StringTypeHandler {
        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
            super.setNonNullParameter(ps, i, Like.AnyLike.MATCH.apply(parameter), jdbcType);
        }
    }

}
