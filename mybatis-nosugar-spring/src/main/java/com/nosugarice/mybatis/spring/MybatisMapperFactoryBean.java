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

package com.nosugarice.mybatis.spring;

import com.nosugarice.mybatis.annotation.SpeedBatch;
import com.nosugarice.mybatis.builder.NoSugarMapperBuilder;
import com.nosugarice.mybatis.builder.mybatis.MutativeMapperProxy;
import com.nosugarice.mybatis.config.ConfigRegistry;
import com.nosugarice.mybatis.config.MapperBuilderConfig;
import com.nosugarice.mybatis.mapper.function.Mapper;
import com.nosugarice.mybatis.spring.config.SpringConfigRegistryBuilder;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/11/15
 */
public class MybatisMapperFactoryBean<T> extends MapperFactoryBean<T> implements ApplicationContextAware {

    private static final Log LOG = LogFactory.getLog(MybatisMapperFactoryBean.class);

    private static ApplicationContext applicationContext = null;
    private static ConfigRegistry configRegistry;

    private MapperBuilderConfig mapperBuilderConfig;
    private NoSugarMapperBuilder mapperBuilder;

    public MybatisMapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }

    @Override
    protected void checkDaoConfig() {
        super.checkDaoConfig();
        mapperBuilder = new NoSugarMapperBuilder(getSqlSession().getConfiguration(), getMapperInterface(), configRegistry);
        mapperBuilderConfig = mapperBuilder.getMapperBuilderConfig();
        if (!mapperBuilderConfig.getSwitchConfig().isLazyBuilder()) {
            mapperBuilder.process();
        }
    }

    @Override
    public T getObject() throws Exception {
        if (!Mapper.class.isAssignableFrom(getMapperInterface())) {
            return super.getObject();
        }
        //可以延迟加载,当使用的时候再进行构建
        if (!mapperBuilder.isLoaded()) {
            mapperBuilder.process();
        }
        T mapper;
        if (mapperBuilderConfig.getSwitchConfig().isSpeedBatch() && getMapperInterface().isAnnotationPresent(SpeedBatch.class)) {
            T defaultObj = super.getObject();
            SqlSession sqlSession = getSqlSessionFactory().openSession(ExecutorType.BATCH);
            T batchMapper = sqlSession.getMapper(getMapperInterface());
            MutativeMapperProxy<T> mutativeMapperProxy = new MutativeMapperProxy<>(getMapperInterface(), defaultObj, batchMapper);
            if (LOG.isDebugEnabled()) {
                LOG.debug("[" + getMapperInterface().getName() + "] " + "已增强批处理功能");
            }
            mapper = mutativeMapperProxy.getObject();
        } else {
            mapper = super.getObject();
        }
        this.mapperBuilderConfig = null;
        this.mapperBuilder = null;
        applicationContext = null;
        configRegistry = null;
        return mapper;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (MybatisMapperFactoryBean.applicationContext == null) {
            MybatisMapperFactoryBean.applicationContext = applicationContext;
            configRegistry = new SpringConfigRegistryBuilder(applicationContext).build();
        }
    }

}
