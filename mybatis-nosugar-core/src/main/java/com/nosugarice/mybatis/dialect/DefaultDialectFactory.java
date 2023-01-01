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

package com.nosugarice.mybatis.dialect;

import com.nosugarice.mybatis.registry.DialectRegistry;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.mapping.MappedStatement;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * 简单实现,性能很低
 *
 * @author dingjingyang@foxmail.com
 * @date 2022/7/10
 */
public class DefaultDialectFactory implements DialectFactory {

    private final DialectRegistry dialectRegistry = new DialectRegistry();

    @Override
    public Dialect getDialect(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            //TODO 可能会有问题,没有一一验证
            return dialectRegistry.chooseDialect(metaData.getDatabaseProductName(), metaData.getDatabaseMajorVersion());
        } catch (SQLException e) {
            throw new BuilderException(e);
        }
    }

    @Override
    public Dialect getDialect(MappedStatement mappedStatement) {
        return getDialect(mappedStatement.getConfiguration().getEnvironment().getDataSource());
    }

}
