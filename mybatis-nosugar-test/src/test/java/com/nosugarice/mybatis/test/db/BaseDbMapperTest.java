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

package com.nosugarice.mybatis.test.db;

import com.nosugarice.mybatis.builder.MapperBuilder;
import com.nosugarice.mybatis.config.MapperBuilderConfigBuilder;
import com.nosugarice.mybatis.config.MetadataBuildingContext;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.Map;
import java.util.Properties;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/6
 */
public abstract class BaseDbMapperTest implements MapperTest {

    private SqlSession sqlSession;

    @BeforeEach
    void setUp() {
        Environment environment = new Environment("test", new JdbcTransactionFactory(), getDataSource());
        Configuration configuration = new Configuration(environment);
        configuration.setVariables(properties());
        configuration.setCacheEnabled(false);
        configuration.setLocalCacheScope(LocalCacheScope.STATEMENT);

        MapperBuilderConfigBuilder configBuilder = new MapperBuilderConfigBuilder(null, configuration.getVariables());
        MetadataBuildingContext metadataBuildingContext = new MetadataBuildingContext(configuration, configBuilder.build());
        for (Class<?> mapper : withMapper()) {
            configuration.addMapper(mapper);
            new MapperBuilder(metadataBuildingContext).process(mapper);
        }
        sqlSession = new SqlSessionFactoryBuilder().build(configuration).openSession();
    }

    @AfterEach
    void tearDown() {
        sqlSession.close();
    }

    private Properties properties() {
        Properties properties = new Properties();
        Map<String, String> propertyMap = getProperties();
        if (propertyMap != null) {
            properties.putAll(propertyMap);
        }
        return properties;
    }

    public Student crateEntity() {
        return new Student();
    }

    public Class<Student> getEntityClass() {
        return Student.class;
    }

    public Class<?>[] withMapper() {
        return new Class<?>[]{
                StudentMapper.class
        };
    }

    public StudentMapper getMapper() {
        return sqlSession.getMapper(StudentMapper.class);
    }

}
