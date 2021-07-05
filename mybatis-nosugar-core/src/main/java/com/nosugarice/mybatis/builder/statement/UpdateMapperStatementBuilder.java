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

import com.nosugarice.mybatis.builder.MapperMetadata;
import com.nosugarice.mybatis.builder.mybatis.VersionSqlSource;
import com.nosugarice.mybatis.builder.sql.SqlScriptBuilder;
import com.nosugarice.mybatis.exception.NoSugarException;
import com.nosugarice.mybatis.mapper.function.Mapper;
import com.nosugarice.mybatis.mapper.update.UpdateByCriteriaMapper;
import com.nosugarice.mybatis.mapper.update.UpdateByPrimaryKeyMapper;
import com.nosugarice.mybatis.mapping.RelationalEntity;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/2
 */
public class UpdateMapperStatementBuilder extends BaseMapperStatementBuilder {

    protected UpdateMapperStatementBuilder(SqlScriptBuilder sqlScriptBuilder, MapperBuilderAssistant assistant) {
        super(sqlScriptBuilder, assistant);
    }

    @Override
    public Collection<Class<? extends Mapper>> getMapperTypes() {
        return Arrays.asList(UpdateByPrimaryKeyMapper.class, UpdateByCriteriaMapper.class);
    }

    @Override
    public SqlCommandType getSqlCommandType(Method method) {
        return SqlCommandType.UPDATE;
    }

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType, Method method) {
        SqlSource sqlSource = super.createSqlSource(configuration, script, parameterType, method);
        if (isVersionMethod(method)) {
            MapperMetadata mapperMetadata = sqlScriptBuilder.getBuildingContext().getMapperMetadata(mapperInterface);
            RelationalEntity relationalEntity = mapperMetadata.getRelationalEntity();
            RelationalProperty property = relationalEntity.getVersionProperty()
                    .orElseThrow(() -> new NoSugarException("未找到版本字段!"));
            sqlSource = new VersionSqlSource(configuration, sqlSource, property);
        }
        return sqlSource;
    }

    private boolean isVersionMethod(Method method) {
        return method.getName().startsWith("update")
                && sqlScriptBuilder.getBuildingContext().getMapperMetadata(mapperInterface).getSupports().isSupportVersion();
    }

}
