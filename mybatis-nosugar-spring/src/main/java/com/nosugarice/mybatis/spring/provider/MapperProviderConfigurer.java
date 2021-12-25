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

package com.nosugarice.mybatis.spring.provider;

import com.nosugarice.mybatis.builder.MapperBuilder;
import com.nosugarice.mybatis.config.MapperBuilderConfig;
import com.nosugarice.mybatis.config.MapperBuilderConfigBuilder;
import com.nosugarice.mybatis.config.MetadataBuildingContext;
import com.nosugarice.mybatis.registry.ConfigRegistry;
import com.nosugarice.mybatis.spring.config.SpringConfigRegistryBuilder;
import com.nosugarice.mybatis.util.CollectionUtils;
import com.nosugarice.mybatis.util.Preconditions;
import com.nosugarice.mybatis.util.StringUtils;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link org.mybatis.spring.mapper.MapperScannerConfigurer}
 *
 * @author dingjingyang@foxmail.com
 * @date 2021/6/26
 */
public class MapperProviderConfigurer implements ApplicationListener<ContextRefreshedEvent> {

    private List<String> basePackages;

    private Class<? extends Annotation> annotationClass;

    private Class<?> markerInterface;

    private String sqlSessionFactoryBeanName;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();

        SqlSessionFactory sqlSessionFactory;
        if (StringUtils.isNotEmpty(sqlSessionFactoryBeanName)) {
            sqlSessionFactory = applicationContext.getBean(sqlSessionFactoryBeanName, SqlSessionFactory.class);
        } else {
            Map<String, SqlSessionFactory> sessionFactoryMap = applicationContext.getBeansOfType(SqlSessionFactory.class);
            Preconditions.checkArgument(!sessionFactoryMap.isEmpty(), "未找到[SqlSessionFactory]实现");
            Preconditions.checkArgument(sessionFactoryMap.size() == 1
                    , "多个[SqlSessionFactory]实现,请指定MapperProvider.sqlSessionFactoryRef");
            sqlSessionFactory = sessionFactoryMap.values().iterator().next();
        }

        Set<BeanDefinition> beanDefinitions = getMapperBeanDefinitions();
        if (CollectionUtils.isEmpty(beanDefinitions)) {
            return;
        }

        Configuration configuration = sqlSessionFactory.getConfiguration();
        MapperRegistry mapperRegistry = configuration.getMapperRegistry();

        List<Class<?>> providerClasses = beanDefinitions.stream()
                .map(BeanDefinition::getBeanClassName)
                .map(className -> {
                    try {
                        return Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        return null;
                    }
                })
                .filter(mapperRegistry::hasMapper)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(providerClasses)) {
            return;
        }
        ConfigRegistry configRegistry = new SpringConfigRegistryBuilder(applicationContext).build();
        MapperBuilderConfig config = new MapperBuilderConfigBuilder(configRegistry, configuration.getVariables()).build();
        MetadataBuildingContext metadataBuildingContext = new MetadataBuildingContext(configuration, config);
        for (Class<?> providerClass : providerClasses) {
            MapperBuilder mapperBuilder = metadataBuildingContext.getMapperBuilder();
            mapperBuilder.process(providerClass);
        }
    }

    private Set<BeanDefinition> getMapperBeanDefinitions() {
        ClassPathMapperScanner mapperScanner = new ClassPathMapperScanner();

        mapperScanner.addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
        if (annotationClass != null) {
            mapperScanner.addIncludeFilter(new AnnotationTypeFilter(annotationClass));
        }
        if (markerInterface != null) {
            mapperScanner.addIncludeFilter(new AssignableTypeFilter(markerInterface));
        }

        boolean isEmpty = basePackages.stream().allMatch(StringUtils::isEmpty);

        if (isEmpty) {
            return mapperScanner.findCandidateComponents("*");
        }

        return basePackages.stream()
                .flatMap(basePackage -> mapperScanner.findCandidateComponents(basePackage).stream())
                .collect(Collectors.toSet());
    }

    public List<String> getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(List<String> basePackages) {
        this.basePackages = basePackages;
    }

    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public Class<?> getMarkerInterface() {
        return markerInterface;
    }

    public void setMarkerInterface(Class<?> markerInterface) {
        this.markerInterface = markerInterface;
    }

    public String getSqlSessionFactoryBeanName() {
        return sqlSessionFactoryBeanName;
    }

    public void setSqlSessionFactoryBeanName(String sqlSessionFactoryBeanName) {
        this.sqlSessionFactoryBeanName = sqlSessionFactoryBeanName;
    }

    private static class ClassPathMapperScanner extends ClassPathScanningCandidateComponentProvider {

        public ClassPathMapperScanner() {
            super(false);
        }

        @Override
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
        }

    }

}
