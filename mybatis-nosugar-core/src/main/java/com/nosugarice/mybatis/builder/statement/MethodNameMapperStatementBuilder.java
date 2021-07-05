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

import com.nosugarice.mybatis.builder.query.parser.PartTree;
import com.nosugarice.mybatis.builder.sql.SqlScriptBuilder;
import com.nosugarice.mybatis.mapper.function.Mapper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.mapping.SqlCommandType;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/2
 */
public class MethodNameMapperStatementBuilder extends BaseMapperStatementBuilder {

    private static final Pattern DELETE_PATTERN = Pattern.compile("^(" + PartTree.DELETE_PATTERN + ")((\\p{Lu}.*?))??By");

    public MethodNameMapperStatementBuilder(SqlScriptBuilder sqlScriptBuilder, MapperBuilderAssistant assistant) {
        super(sqlScriptBuilder, assistant);
    }

    @Override
    public Collection<Class<? extends Mapper>> getMapperTypes() {
        return Collections.emptyList();
    }

    @Override
    public SqlCommandType getSqlCommandType(Method method) {
        SqlCommandType sqlCommandType = SqlCommandType.SELECT;
        if (DELETE_PATTERN.matcher(method.getName()).find()) {
            sqlCommandType = SqlCommandType.DELETE;
        }
        return sqlCommandType;
    }
}
