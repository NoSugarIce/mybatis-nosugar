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

import com.nosugarice.mybatis.builder.query.ConditionType;
import com.nosugarice.mybatis.builder.query.parser.Part;
import com.nosugarice.mybatis.builder.query.parser.PartTree;
import com.nosugarice.mybatis.builder.query.parser.Subject;
import com.nosugarice.mybatis.builder.sql.AbstractRenderingContext;
import com.nosugarice.mybatis.builder.sql.SqlPart;
import com.nosugarice.mybatis.builder.sql.SqlScriptBuilder;
import com.nosugarice.mybatis.builder.statement.BaseMapperStatementBuilder;
import com.nosugarice.mybatis.builder.statement.MapperStatementFactory;
import com.nosugarice.mybatis.config.MetadataBuildingContext;
import com.nosugarice.mybatis.domain.Page;
import com.nosugarice.mybatis.exception.NoSugarException;
import com.nosugarice.mybatis.mapper.function.MethodNameMapper;
import com.nosugarice.mybatis.mapping.RelationalEntity;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.query.CriterionSqlUtils;
import com.nosugarice.mybatis.query.criterion.AnywhereLike;
import com.nosugarice.mybatis.query.criterion.EndLike;
import com.nosugarice.mybatis.query.criterion.GroupCriterionImpl;
import com.nosugarice.mybatis.query.criterion.NotAnywhereLike;
import com.nosugarice.mybatis.query.criterion.StartLike;
import com.nosugarice.mybatis.sql.RenderingContext;
import com.nosugarice.mybatis.sql.SqlBuilder;
import com.nosugarice.mybatis.sql.SqlTempLateService;
import com.nosugarice.mybatis.sql.criterion.Criterion;
import com.nosugarice.mybatis.sql.criterion.GroupCriterion;
import com.nosugarice.mybatis.sql.criterion.PropertyCriterion;
import com.nosugarice.mybatis.sql.criterion.PropertyCriterionVisitor;
import com.nosugarice.mybatis.util.Preconditions;
import com.nosugarice.mybatis.util.StringFormatter;
import com.nosugarice.mybatis.util.StringUtils;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 * 简单实现
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/12/5
 */
public class MapperMethodNameBuilder extends AbstractMapperBuilder {

    private final ThreadLocal<String> whereSqlThreadLocal = new ThreadLocal<>();

    private final RelationalEntity relationalEntity;
    private final BaseMapperStatementBuilder statementBuilder;

    private static final Map<Class<? extends PropertyCriterion<?>>, Class<? extends TypeHandler<?>>> CRITERION_TYPE_HANDLER_MAP
            = new HashMap<Class<? extends PropertyCriterion<?>>, Class<? extends TypeHandler<?>>>() {
        private static final long serialVersionUID = -3916050540685606900L;

        {
            put(StartLike.class, com.nosugarice.mybatis.builder.mybatis.TypeHandler.StartLikeTypeHandler.class);
            put(EndLike.class, com.nosugarice.mybatis.builder.mybatis.TypeHandler.EenLikeTypeHandler.class);
            put(AnywhereLike.class, com.nosugarice.mybatis.builder.mybatis.TypeHandler.AnywhereLikeTypeHandler.class);
            put(NotAnywhereLike.class, com.nosugarice.mybatis.builder.mybatis.TypeHandler.AnywhereLikeTypeHandler.class);
        }
    };

    public MapperMethodNameBuilder(MetadataBuildingContext buildingContext, Class<?> mapperInterface) {
        super(buildingContext, mapperInterface);
        this.relationalEntity = buildingContext.getMapperMetadata(mapperInterface).getRelationalEntity();
        this.statementBuilder = MapperStatementFactory.getMapperStatementBuilder(MethodNameMapper.class
                , buildingContext.getSqlScriptBuilder(mapperInterface)
                , buildingContext.getMapperBuilderAssistant(mapperInterface));
    }

    @Override
    public boolean isNeedAchieveMethod(Method method) {
        return MethodNameMapper.class.isAssignableFrom(method.getDeclaringClass())
                && !method.isAnnotationPresent(SqlBuilder.class)
                && !configuration.hasStatement(getMethodMappedStatementId(method))
                && PartTree.PREFIX_TEMPLATE.matcher(method.getName()).find();
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
            PartTree.Predicate predicate = partTree.getPredicate();
            List<Part> parts = new ArrayList<>();
            predicate.getNodes().forEach(orPart -> parts.addAll(orPart.getChildren()));
            parts.forEach(part -> Preconditions.checkArgument(relationalEntity.existPropertyName(part.getPropertyName())
                    , true, String.format("No property %s found for type %s!", part.getPropertyName()
                            , relationalEntity.getName())));
            int totalNumberOfParameters = parts.stream()
                    .mapToInt(part -> part.getConditionType().getValueType().getNumberOfParameters()).sum();

            long parameterCount = Arrays.stream(method.getParameterTypes())
                    .filter(clazz -> !Page.class.isAssignableFrom(clazz))
                    .count();
            Preconditions.checkArgument(totalNumberOfParameters == parameterCount, true
                    , String.format("Method %s expects at least %d arguments but only found %d."
                            , methodName, totalNumberOfParameters, parameterCount));

            List<PartTree.OrPart> nodes = predicate.getNodes();
            List<GroupCriterion> groupCriteria = creatGroupCriteria(nodes);
            String whereSql = getWhereSql(method, groupCriteria);
            whereSqlThreadLocal.set(whereSql);

            SqlScriptBuilder sqlScriptBuilder = statementBuilder.getSqlScriptBuilder();
            Subject subject = partTree.getSubject();
            if (subject.isDelete()) {
                sqlScriptBuilder.bind(method, this::provideDelete);
            } else if (subject.isCount() || subject.isExists()) {
                sqlScriptBuilder.bind(method, this::provideCount);
            } else {
                sqlScriptBuilder.bind(method, this::provideFind);
            }
            statementBuilder.addMappedStatement(method);
            whereSqlThreadLocal.remove();
        }
    }

    private String provideFind(SqlTempLateService sqlTempLate) {
        String sql = sqlTempLate.provideFind();
        Map<String, String> placeholderValues = new HashMap<>(1, 1);
        placeholderValues.put(SqlPart.Placeholder.JPA_WHERE, getWhereSql());
        return StringFormatter.replacePlaceholder(sql, placeholderValues);
    }

    private String provideCount(SqlTempLateService sqlTempLate) {
        String sql = sqlTempLate.provideCount();
        Map<String, String> placeholderValues = new HashMap<>(1, 1);
        placeholderValues.put(SqlPart.Placeholder.JPA_WHERE, getWhereSql());
        return StringFormatter.replacePlaceholder(sql, placeholderValues);
    }

    private String provideDelete(SqlTempLateService sqlTempLate) {
        String sql = sqlTempLate.provideDelete();
        Map<String, String> placeholderValues = new HashMap<>(1, 1);
        placeholderValues.put(SqlPart.Placeholder.JPA_WHERE, getWhereSql());
        return StringFormatter.replacePlaceholder(sql, placeholderValues);
    }

    private String getWhereSql() {
        String whereSql = whereSqlThreadLocal.get();
        Preconditions.checkArgument(StringUtils.isNotBlank(whereSql), true, "没有正确构建whereSql");
        return whereSql;
    }

    private String getWhereSql(Method method, List<GroupCriterion> groupCriterions) {
        ParamNameResolver paramNameResolver = new ParamNameResolver(configuration, method);
        String[] names = paramNameResolver.getNames();

        Iterator<String> paramNameIterator = Stream.of(names).collect(Collectors.toList()).iterator();
        Map<String, Class<? extends TypeHandler<?>>> columnTypeHandlerMap = getColumnTypeHandler(groupCriterions);

        RenderingContext renderingContext = new MethodNameRenderingContext(relationalEntity.getEntityClass().getClassType()
                , paramNameIterator, columnTypeHandlerMap);
        PropertyCriterionVisitor<String> visitor = new MethodNameRenderPlaceholderVisitor(renderingContext);

        return CriterionSqlUtils.getCriterionSql(visitor, groupCriterions);
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
                RelationalProperty relationalProperty = relationalEntity.getPropertyByPropertyName(child.getPropertyName());
                Class<? extends PropertyCriterion<?>> propertyCriterionTye = conditionType.getPropertyCriterionTye();
                try {
                    Constructor<? extends PropertyCriterion<?>> constructor = propertyCriterionTye.getConstructor(String.class);
                    PropertyCriterion<?> propertyCriterion = constructor.newInstance(relationalProperty.getColumn().getName());
                    groupCriterion.append(propertyCriterion);
                } catch (Exception e) {
                    throw new NoSugarException(e);
                }
            }
        }
        return groupCriteria;
    }

    private Map<String, Class<? extends TypeHandler<?>>> getColumnTypeHandler(List<GroupCriterion> groupCriteria) {
        List<PropertyCriterion<?>> propertyCriterionList = groupCriteria.stream()
                .map(GroupCriterion::getCriterions)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        Map<String, Class<? extends TypeHandler<?>>> columnTypeHandlerMap = new HashMap<>(propertyCriterionList.size());
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        for (PropertyCriterion<?> propertyCriterion : propertyCriterionList) {
            Class<?> javaType = relationalEntity.getPropertyByColumnName(propertyCriterion.getColumn()).getJavaType();
            Class<? extends TypeHandler<?>> typeHandlerType = CRITERION_TYPE_HANDLER_MAP.get(propertyCriterion.getClass());
            if (typeHandlerType != null) {
                columnTypeHandlerMap.put(propertyCriterion.getColumn(), typeHandlerType);
                TypeHandler<?> typeHandler = typeHandlerRegistry.getMappingTypeHandler(typeHandlerType);
                if (typeHandler == null) {
                    typeHandler = typeHandlerRegistry.getInstance(javaType, typeHandlerType);
                    typeHandlerRegistry.register(typeHandler);
                }
            }
        }
        return columnTypeHandlerMap;
    }

    private static class MethodNameRenderPlaceholderVisitor implements PropertyCriterionVisitor<String> {

        private final RenderingContext renderingContext;

        private MethodNameRenderPlaceholderVisitor(RenderingContext renderingContext) {
            this.renderingContext = renderingContext;
        }

        @Override
        public String visit(PropertyCriterion<?> propertyCriterion) {
            return propertyCriterion.renderPlaceholder(renderingContext);
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
            return getSingleValuePlaceholder(column) + SqlPart.AND + getSingleValuePlaceholder(column);
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
            return SqlPart.EMPTY;
        }

    }

}
