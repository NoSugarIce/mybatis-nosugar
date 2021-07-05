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

import com.nosugarice.mybatis.builder.sql.SqlScriptBuilder;
import com.nosugarice.mybatis.mapper.delete.DeleteMapper;
import com.nosugarice.mybatis.mapper.function.MethodNameMapper;
import com.nosugarice.mybatis.mapper.insert.InsertMapper;
import com.nosugarice.mybatis.mapper.logicdelete.LogicDeleteMapper;
import com.nosugarice.mybatis.mapper.select.SelectMapper;
import com.nosugarice.mybatis.mapper.update.UpdateMapper;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.MapperBuilderAssistant;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/31
 */
public class MapperStatementFactory {

    public static BaseMapperStatementBuilder getMapperStatementBuilder(Class<?> mapperType, SqlScriptBuilder sqlScriptBuilder
            , MapperBuilderAssistant assistant) {
        if (SelectMapper.class.isAssignableFrom(mapperType)) {
            return new SelectMapperStatementBuilder(sqlScriptBuilder, assistant);
        }
        if (InsertMapper.class.isAssignableFrom(mapperType)) {
            return new InsertMapperStatementBuilder(sqlScriptBuilder, assistant);
        }
        if (UpdateMapper.class.isAssignableFrom(mapperType)) {
            return new UpdateMapperStatementBuilder(sqlScriptBuilder, assistant);
        }
        if (DeleteMapper.class.isAssignableFrom(mapperType)) {
            return new DeleteMapperStatementBuilder(sqlScriptBuilder, assistant);
        }
        if (LogicDeleteMapper.class.isAssignableFrom(mapperType)) {
            return new LogicDeleteMapperStatementBuilder(sqlScriptBuilder, assistant);
        }
        if (MethodNameMapper.class.isAssignableFrom(mapperType)) {
            return new MethodNameMapperStatementBuilder(sqlScriptBuilder, assistant);
        }
        throw new BuilderException("");
    }

}
