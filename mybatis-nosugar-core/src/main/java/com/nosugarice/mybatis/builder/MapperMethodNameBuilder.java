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

package com.nosugarice.mybatis.builder;

import com.nosugarice.mybatis.builder.sql.SqlScriptBuilder;
import com.nosugarice.mybatis.builder.statement.MapperStatementBuilder;
import com.nosugarice.mybatis.builder.statement.MapperStatementFactory;
import com.nosugarice.mybatis.domain.Page;
import com.nosugarice.mybatis.exception.NoSugarException;
import com.nosugarice.mybatis.mapper.function.FunS;
import com.nosugarice.mybatis.mapper.function.MethodNameMapper;
import com.nosugarice.mybatis.mapping.RelationalEntity;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.query.criterion.AnywhereLike;
import com.nosugarice.mybatis.query.criterion.ColumnCriterion;
import com.nosugarice.mybatis.query.criterion.ColumnCriterionVisitor;
import com.nosugarice.mybatis.query.criterion.Criterion;
import com.nosugarice.mybatis.query.criterion.EndLike;
import com.nosugarice.mybatis.query.criterion.GroupCriterion;
import com.nosugarice.mybatis.query.criterion.GroupCriterionImpl;
import com.nosugarice.mybatis.query.criterion.Like;
import com.nosugarice.mybatis.query.criterion.NotAnywhereLike;
import com.nosugarice.mybatis.query.criterion.StartLike;
import com.nosugarice.mybatis.query.jpa.ConditionType;
import com.nosugarice.mybatis.query.jpa.parser.OrderBySource;
import com.nosugarice.mybatis.query.jpa.parser.Part;
import com.nosugarice.mybatis.query.jpa.parser.PartTree;
import com.nosugarice.mybatis.query.jpa.parser.PartTree.Predicate;
import com.nosugarice.mybatis.query.jpa.parser.Subject;
import com.nosugarice.mybatis.query.process.SortCriterion;
import com.nosugarice.mybatis.sql.CriterionSqlUtils;
import com.nosugarice.mybatis.sql.ProviderTempLate;
import com.nosugarice.mybatis.sql.RenderingContext;
import com.nosugarice.mybatis.sql.SqlAndParameterBind;
import com.nosugarice.mybatis.sql.SqlBuilder;
import com.nosugarice.mybatis.sql.SqlPart;
import com.nosugarice.mybatis.sql.render.AbstractRenderingContext;
import com.nosugarice.mybatis.util.Preconditions;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.StringTypeHandler;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
public class MapperMethodNameBuilder extends AbstractMapperBuilder<MapperMethodNameBuilder> {

    private RelationalEntity relationalEntity;
    private MapperStatementBuilder statementBuilder;

    private static final Map<Class<? extends ColumnCriterion<?>>, Class<? extends TypeHandler<?>>> CRITERION_TYPE_HANDLER_MAP
            = new HashMap<>();

    static {
        CRITERION_TYPE_HANDLER_MAP.put(StartLike.class, StartLikeTypeHandler.class);
        CRITERION_TYPE_HANDLER_MAP.put(EndLike.class, EenLikeTypeHandler.class);
        CRITERION_TYPE_HANDLER_MAP.put(AnywhereLike.class, AnywhereLikeTypeHandler.class);
        CRITERION_TYPE_HANDLER_MAP.put(NotAnywhereLike.class, AnywhereLikeTypeHandler.class);
    }

    @Override
    public MapperMethodNameBuilder build() {
        super.build();
        this.relationalEntity = entityMetadata.getRelationalEntity();
        this.statementBuilder = MapperStatementFactory.getMapperStatementBuilder(mapperClass, MethodNameMapper.class, buildingContext);
        return this;
    }

    @Override
    public boolean isMapper() {
        return MethodNameMapper.class.isAssignableFrom(mapperClass);
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
            int totalNumberOfParameters = parts.stream()
                    .mapToInt(part -> part.getConditionType().getValueType().getNumberOfParameters()).sum();

            long parameterCount = Arrays.stream(method.getParameterTypes())
                    .filter(clazz -> !Page.class.isAssignableFrom(clazz))
                    .count();
            Preconditions.checkArgument(totalNumberOfParameters == parameterCount
                    , String.format("Method %s expects at least %d arguments but only found %d."
                            , methodName, totalNumberOfParameters, parameterCount));
            predicate.getOrderBySource().map(OrderBySource::getOrderMap).ifPresent(orderMap -> orderMap.keySet().forEach(propertyName -> Preconditions.checkArgument(entityMetadata.existProperty(propertyName)
                    , String.format("No property %s found for type %s!", propertyName, relationalEntity.getName()))));

            List<PartTree.OrPart> nodes = predicate.getNodes();
            List<GroupCriterion> groupCriteria = creatGroupCriteria(nodes);
            String whereSql = getWhereSql(method, groupCriteria);

            SqlScriptBuilder sqlScriptBuilder = statementBuilder.getSqlScriptBuilder();
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
                String orderBy = predicate.getOrderBySource().map(OrderBySource::getOrderMap).map(orderMap -> {
                    SortCriterion sortCriterion = new SortCriterion();
                    orderMap.forEach((property, asc) -> sortCriterion.append(asc
                            , entityMetadata.getPropertyByPropertyName(property).getColumn()));
                    return sortCriterion.getSql();
                }).orElse(EMPTY);
                sqlAndParameterBind = sqlScriptBuilder.build(providerFunParam4, whereSql, orderBy, limit);
            }
            if (sqlAndParameterBind == null) {
                Preconditions.checkNotNull(providerFun, "未设置构建SQL方法.");
                sqlAndParameterBind = sqlScriptBuilder.build(providerFun, whereSql);
            }
            statementBuilder.addMappedStatement(method, SqlPart.script(sqlAndParameterBind.getSql()));
        }
    }

    private String getWhereSql(Method method, List<GroupCriterion> groupCriterions) {
        String[] parameterNames = new ParamNameResolver(configuration, method).getNames();

        Iterator<String> paramNameIterator = Stream.of(parameterNames).collect(Collectors.toList()).iterator();
        Map<String, Class<? extends TypeHandler<?>>> columnTypeHandlerMap = getColumnTypeHandler(groupCriterions);

        RenderingContext renderingContext = new MethodNameRenderingContext(entityMetadata.getEntityClass()
                , paramNameIterator, columnTypeHandlerMap);
        ColumnCriterionVisitor<String> visitor = new MethodNameRenderPlaceholderVisitor(renderingContext);

        return CriterionSqlUtils.getCriterionSql(groupCriterions, visitor, Function.identity());
    }

    private List<GroupCriterion> creatGroupCriteria(List<PartTree.OrPart> nodes) {
        List<GroupCriterion> groupCriteria = new ArrayList<>();
        for (PartTree.OrPart node : nodes) {
            GroupCriterion groupCriterion = new GroupCriterionImpl();
            groupCriterion.setSeparator(Criterion.Separator.OR);
            groupCriteria.add(groupCriterion);
            List<Part> children = node.getChildren();
            for (Part child : children) {
                ConditionType conditionType = child.getConditionType();
                RelationalProperty relationalProperty = entityMetadata.getPropertyByPropertyName(child.getPropertyName());
                Class<? extends ColumnCriterion<?>> propertyCriterionTye = conditionType.getPropertyCriterionTye();
                try {
                    Constructor<? extends ColumnCriterion<?>> constructor = propertyCriterionTye.getConstructor(String.class);
                    ColumnCriterion<?> columnCriterion = constructor.newInstance(relationalProperty.getColumn());
                    groupCriterion.append(columnCriterion);
                } catch (Exception e) {
                    throw new NoSugarException(e);
                }
            }
        }
        return groupCriteria;
    }

    private Map<String, Class<? extends TypeHandler<?>>> getColumnTypeHandler(List<GroupCriterion> groupCriteria) {
        List<ColumnCriterion<?>> columnCriterionList = groupCriteria.stream()
                .map(GroupCriterion::getCriterions)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

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

    private static class MethodNameRenderPlaceholderVisitor implements ColumnCriterionVisitor<String> {

        private final RenderingContext renderingContext;

        private MethodNameRenderPlaceholderVisitor(RenderingContext renderingContext) {
            this.renderingContext = renderingContext;
        }

        @Override
        public String visit(ColumnCriterion<?> columnCriterion) {
            return columnCriterion.renderPlaceholder(renderingContext);
        }
    }

    private static class MethodNameRenderingContext extends AbstractRenderingContext {

        /** 加上jdbcType=OTHER 为了不覆盖默认的处理类型 */
        private static final String CLASH_ASSIGN_JDBC_TYPE = "jdbcType=OTHER";

        private final Map<String, Class<? extends TypeHandler<?>>> columnTypeHandlerMap;

        public MethodNameRenderingContext(Class<?> entityClass, Iterator<String> paramNameIterator
                , Map<String, Class<? extends TypeHandler<?>>> columnTypeHandlerMap) {
            super(entityClass, paramNameIterator);
            this.columnTypeHandlerMap = columnTypeHandlerMap;
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
                    assignTypeHandler = SqlPart.assignTypeHandler(typeHandlerType);
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

    @Override
    public int getOrder() {
        return 10;
    }

    @MappedJdbcTypes(value = JdbcType.OTHER)
    public static class StartLikeTypeHandler extends StringTypeHandler {
        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
            super.setNonNullParameter(ps, i, Like.MatchMode.START.toMatchString(parameter), jdbcType);
        }
    }

    @MappedJdbcTypes(value = JdbcType.OTHER)
    public static class EenLikeTypeHandler extends StringTypeHandler {
        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
            super.setNonNullParameter(ps, i, Like.MatchMode.END.toMatchString(parameter), jdbcType);
        }
    }

    @MappedJdbcTypes(value = JdbcType.OTHER)
    public static class AnywhereLikeTypeHandler extends StringTypeHandler {
        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
            super.setNonNullParameter(ps, i, Like.MatchMode.ANYWHERE.toMatchString(parameter), jdbcType);
        }
    }

}
