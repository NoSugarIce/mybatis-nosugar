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

package com.nosugarice.mybatis.spring;

import com.nosugarice.mybatis.annotation.SpeedBatch;
import com.nosugarice.mybatis.builder.MapperBuilder;
import com.nosugarice.mybatis.config.MapperBuilderConfig;
import com.nosugarice.mybatis.config.MapperBuilderConfigBuilder;
import com.nosugarice.mybatis.config.MetadataBuildingContext;
import com.nosugarice.mybatis.mapper.function.Mapper;
import com.nosugarice.mybatis.registry.ConfigRegistry;
import com.nosugarice.mybatis.spring.config.SpringConfigRegistryBuilder;
import org.apache.ibatis.annotations.Flush;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/11/15
 */
public class MybatisMapperFactoryBean<T> extends MapperFactoryBean<T> implements ApplicationContextAware {

    private static final Log LOG = LogFactory.getLog(MybatisMapperFactoryBean.class);

    private static final Map<Configuration, MetadataBuildingContext> BUILDING_CONTEXT_MAP = new ConcurrentHashMap<>();

    private static final Map<SqlSessionFactory, SqlSessionTemplate> BATCH_SQL_SESSION_TEMPLATE_MAP = new ConcurrentHashMap<>();

    private static ConfigRegistry configRegistry;

    private SqlSessionFactory sqlSessionFactory;

    private MetadataBuildingContext metadataBuildingContext;

    public MybatisMapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }

    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Override
    protected void checkDaoConfig() {
        if (getSqlSessionTemplate() == null && sqlSessionFactory != null) {
            setSqlSessionTemplate(createSqlSessionTemplate(sqlSessionFactory));
        }
        if (getSqlSessionTemplate() != null && sqlSessionFactory != null && sqlSessionFactory != getSqlSessionTemplate().getSqlSessionFactory()) {
            setSqlSessionTemplate(createSqlSessionTemplate(sqlSessionFactory));
        }
        super.checkDaoConfig();
    }

    @Override
    protected void initDao() {
        this.metadataBuildingContext = BUILDING_CONTEXT_MAP.computeIfAbsent(getSqlSession().getConfiguration(), configuration -> {
            MapperBuilderConfig mapperBuilderConfig = new MapperBuilderConfigBuilder(configRegistry
                    , getSqlSession().getConfiguration().getVariables()).build();
            return new MetadataBuildingContext(getSqlSession().getConfiguration(), mapperBuilderConfig);
        });
        MapperBuilder mapperBuilder = metadataBuildingContext.getMapperBuilder();
        if (!metadataBuildingContext.getConfig().getSwitchConfig().isLazyBuilder()) {
            mapperBuilder.process(getMapperInterface());
        }
    }

    @Override
    public T getObject() throws Exception {
        Class<T> mapperInterface = getMapperInterface();
        if (!Mapper.class.isAssignableFrom(mapperInterface)) {
            return super.getObject();
        }
        MapperBuilder mapperBuilder = metadataBuildingContext.getMapperBuilder();
        //可以延迟加载,当使用的时候再进行构建
        if (!mapperBuilder.isLoaded(mapperInterface)) {
            mapperBuilder.process(mapperInterface);
        }
        T mapper = super.getObject();
        if (metadataBuildingContext.getConfig().getSwitchConfig().isSpeedBatch() && mapperInterface.isAnnotationPresent(SpeedBatch.class)) {
            SqlSession sqlSession = BATCH_SQL_SESSION_TEMPLATE_MAP.computeIfAbsent(getSqlSessionFactory(), sqlSessionFactory
                    -> new SqlSessionTemplate(new DefaultSqlSessionFactory(getSqlSessionFactory().getConfiguration()), ExecutorType.BATCH));
            T batchMapper = sqlSession.getMapper(mapperInterface);
            MutativeMapperProxy<T> mutativeMapperProxy = new MutativeMapperProxy<>(mapperInterface, mapper, batchMapper);
            if (LOG.isDebugEnabled()) {
                LOG.debug("[" + mapperInterface.getName() + "] " + "已增强批处理功能");
            }
            mapper = mutativeMapperProxy.getObject();
        }
        return mapper;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (MybatisMapperFactoryBean.configRegistry == null) {
            configRegistry = new SpringConfigRegistryBuilder(applicationContext).build();
        }
    }

    private static class MutativeMapperProxy<T> implements InvocationHandler {

        private final Class<T> mapperInterface;
        private final T defaultMapperProxy;
        private final T batchMapperProxy;

        public MutativeMapperProxy(Class<T> mapperInterface, T defaultMapperProxy, T batchMapperProxy) {
            this.mapperInterface = mapperInterface;
            this.defaultMapperProxy = defaultMapperProxy;
            this.batchMapperProxy = batchMapperProxy != null ? batchMapperProxy : defaultMapperProxy;
        }

        @SuppressWarnings("unchecked")
        public T getObject() {
            return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.isAnnotationPresent(SpeedBatch.class) || method.isAnnotationPresent(Flush.class)) {
                return method.invoke(batchMapperProxy, args);
            } else {
                return method.invoke(defaultMapperProxy, args);
            }
        }

    }

}
