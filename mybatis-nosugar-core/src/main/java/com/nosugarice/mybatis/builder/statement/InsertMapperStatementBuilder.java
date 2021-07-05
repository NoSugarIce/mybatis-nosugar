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

package com.nosugarice.mybatis.builder.statement;

import com.nosugarice.mybatis.builder.mybatis.CustomizeKeyGenerator;
import com.nosugarice.mybatis.builder.sql.SqlScriptBuilder;
import com.nosugarice.mybatis.config.Supports;
import com.nosugarice.mybatis.dialect.Dialect;
import com.nosugarice.mybatis.dialect.Identity;
import com.nosugarice.mybatis.exception.NoSugarException;
import com.nosugarice.mybatis.mapper.function.Mapper;
import com.nosugarice.mybatis.mapper.insert.InsertMapper;
import com.nosugarice.mybatis.mapping.Column;
import com.nosugarice.mybatis.mapping.RelationalEntity;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.mapping.value.KeyValue;
import com.nosugarice.mybatis.util.Preconditions;
import com.nosugarice.mybatis.util.StringUtils;
import com.nosugarice.mybatis.valuegenerator.id.IdGenerator;
import com.nosugarice.mybatis.valuegenerator.id.IdGeneratorRegistry;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/2
 */
public class InsertMapperStatementBuilder extends BaseMapperStatementBuilder {

    private final RelationalEntity relationalEntity;

    protected InsertMapperStatementBuilder(SqlScriptBuilder sqlScriptBuilder, MapperBuilderAssistant assistant) {
        super(sqlScriptBuilder, assistant);
        this.relationalEntity = sqlScriptBuilder.getMapperBuildRaw().getRelationalEntity();
    }

    @Override
    public Collection<Class<? extends Mapper>> getMapperTypes() {
        return Collections.singletonList(InsertMapper.class);
    }

    @Override
    public SqlCommandType getSqlCommandType(Method method) {
        return SqlCommandType.INSERT;
    }

    @Override
    public KeyGenerator getKeyGenerator(String mappedName) {
        String id = mappedName + SelectKeyGenerator.SELECT_KEY_SUFFIX;
        if (configuration.hasKeyGenerator(id)) {
            return configuration.getKeyGenerator(id);
        }
        KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
        RelationalEntity relationalEntity = sqlScriptBuilder.getMapperBuildRaw().getRelationalEntity();
        Supports supports = sqlScriptBuilder.getMapperBuildRaw().getSupports();
        if (supports.isSupportAutoIncrement()) {
            Dialect dialect = sqlScriptBuilder.getBuildingContext().getDialect();
            Identity identity = dialect.getIdentity();
            boolean autoIncrement = identity.supportsAutoIncrement();
            if (autoIncrement) {
                keyGenerator = Jdbc3KeyGenerator.INSTANCE;
            } else {
                RelationalProperty relationalProperty = relationalEntity.getIdGeneratorProperty()
                        .orElseThrow(() -> new NoSugarException("未找到自增主键"));
                KeyValue<?> keyValue = relationalProperty.getAsKeyValue();
                String sql = keyValue.getGenerator();
                if (identity.supportsSelectIdentity()) {
                    if (StringUtils.isEmpty(sql)) {
                        sql = identity.getIdentitySelectString();
                    }
                } else {
                    Preconditions.checkArgument(StringUtils.isNotEmpty(sql), true
                            , relationalProperty.getRelationalModel().getEntityClass().getName()
                                    + "." + relationalProperty.getName() + "没有设置主键生成语句");
                }

                MappedStatement keyStatement = getKeyMappedStatement(id, relationalProperty.getJavaType(), sql);

                keyGenerator = new SelectKeyGenerator(keyStatement, identity.executeBeforeIdentitySelect());
                configuration.addKeyGenerator(id, keyGenerator);
            }
        } else if (supports.isSupportIdGenerator()) {
            RelationalProperty relationalProperty = relationalEntity.getIdGeneratorProperty()
                    .orElseThrow(() -> new NoSugarException("未找到生成策略主键!"));
            KeyValue<?> keyValue = relationalProperty.getAsKeyValue();
            IdGeneratorRegistry idGeneratorRegistry = sqlScriptBuilder.getBuildingContext().getIdGeneratorRegistry();
            IdGenerator<?> idGenerator = idGeneratorRegistry.getObject(keyValue.getGenerator());

            MappedStatement keyStatement = getKeyMappedStatement(id, relationalProperty.getJavaType(), null);

            keyGenerator = new CustomizeKeyGenerator(keyStatement, idGenerator);
            configuration.addKeyGenerator(id, keyGenerator);
        }
        return keyGenerator;
    }

    @Override
    public String getKeyProperty() {
        return relationalEntity.getIdGeneratorProperty()
                .map(RelationalProperty::getName)
                .orElse(null);
    }

    @Override
    public String getKeyColumn() {
        return relationalEntity.getIdGeneratorProperty()
                .map(RelationalProperty::getColumn)
                .map(Column::getName)
                .orElse(null);
    }

    private MappedStatement getKeyMappedStatement(String id, Class<?> propertyType, String sql) {
        ResultMap.Builder resultMapBuilder = new ResultMap.Builder(configuration, id + "-Inline", propertyType, new ArrayList<>());
        List<ResultMap> resultMaps = new ArrayList<>();
        resultMaps.add(resultMapBuilder.build());

        SqlSource sqlSource = new StaticSqlSource(configuration, sql);
        MappedStatement mappedStatement = new MappedStatement.Builder(configuration, id, sqlSource, SqlCommandType.SELECT)
                .statementType(StatementType.STATEMENT)
                .keyGenerator(NoKeyGenerator.INSTANCE)
                .keyProperty(getKeyProperty())
                .keyColumn(getKeyColumn())
                .resultMaps(resultMaps)
                .build();

        configuration.addMappedStatement(mappedStatement);
        return configuration.getMappedStatement(id, false);
    }

}
