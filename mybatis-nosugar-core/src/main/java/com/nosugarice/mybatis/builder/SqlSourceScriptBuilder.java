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

import com.nosugarice.mybatis.config.EntityMetadata;
import com.nosugarice.mybatis.config.MetadataBuildingContext;
import com.nosugarice.mybatis.mapper.function.FunS;
import com.nosugarice.mybatis.sql.Placeholder;
import com.nosugarice.mybatis.sql.ProviderTempLate;
import com.nosugarice.mybatis.sql.ProviderTempLateImpl;
import com.nosugarice.mybatis.sql.SqlAndParameterBind;
import com.nosugarice.mybatis.sql.SqlBuilder;
import com.nosugarice.mybatis.sqlsource.DynamicHandlerSqlSource;
import com.nosugarice.mybatis.support.DynamicTableNameMapping;
import com.nosugarice.mybatis.util.Preconditions;
import com.nosugarice.mybatis.util.StringFormatter;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.reflection.ParamNameResolver;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/2
 */
public class SqlSourceScriptBuilder {

    private Map<Method, FunS<SqlAndParameterBind>> providerFunMap;

    private final MetadataBuildingContext buildingContext;
    private final EntityMetadata entityMetadata;
    private final ProviderTempLate providerTempLate;

    public static final Object PLACEHOLDER_OBJECT = new Object();

    public SqlSourceScriptBuilder(EntityMetadata entityMetadata, MetadataBuildingContext buildingContext) {
        this.buildingContext = buildingContext;
        this.entityMetadata = entityMetadata;
        this.providerTempLate = new ProviderTempLateImpl(entityMetadata, buildingContext.getDialect());
    }

    public void bind(Method method, FunS<SqlAndParameterBind> providerFun) {
        if (providerFunMap == null) {
            providerFunMap = new ConcurrentHashMap<>(32);
        }
        providerFunMap.put(method, providerFun);
    }

    public SqlAndParameterBind build(FunS<SqlAndParameterBind> providerFun, Object... args) {
        Object[] objects = new Object[args.length + 1];
        System.arraycopy(args, 0, objects, 1, args.length);
        objects[0] = providerTempLate;
        return providerFun.invoke(objects);
    }

    private SqlAndParameterBind build(Method method, Object... args) {
        FunS<SqlAndParameterBind> providerFun = providerFunMap.get(method);
        Preconditions.checkNotNull(providerFun, "[" + method.getDeclaringClass() + "." + method.getName() + "]"
                + "未找到构建SQL实现!");
        return build(providerFun, args);
    }

    public SqlSource buildSqlSource(Method method, SqlCommandType sqlCommandType) {
        SqlSource sqlSource;
        SqlBuilder sqlBuilder = method.getAnnotation(SqlBuilder.class);
        if (sqlBuilder != null) {
            String[] parameterNames = new ParamNameResolver(buildingContext.getConfiguration(), method).getNames();
            sqlSource = new DynamicHandlerSqlSource(sqlCommandType, buildingContext, parameterNames
                    , sqlBuilder.sqlFunction().providerFun(), this, sqlBuilder.fixedParameter());
        } else {
            String script = build(method).getSql();
            sqlSource = new StaticSqlSource(buildingContext.getConfiguration(), script);
        }
        if (entityMetadata.getSupports().isSupportDynamicTableName() && sqlSource instanceof DynamicHandlerSqlSource) {
            ((DynamicHandlerSqlSource) sqlSource).addSqlHandler(sql -> {
                String runTimeTableName = DynamicTableNameMapping.getName(entityMetadata.getRelationalEntity().getTable().getName());
                Map<String, String> data = new HashMap<>(1, 1);
                data.put(Placeholder.TABLE_P, runTimeTableName);
                return StringFormatter.replacePlaceholder(sql, data);
            });
        }
        return sqlSource;
    }

}
