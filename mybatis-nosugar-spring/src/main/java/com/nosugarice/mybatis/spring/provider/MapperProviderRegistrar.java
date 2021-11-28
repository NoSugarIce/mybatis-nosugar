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

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link org.mybatis.spring.annotation.MapperScannerRegistrar}
 *
 * @author dingjingyang@foxmail.com
 * @date 2021/6/26
 */
public class MapperProviderRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annAttrs = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(MapperProvider.class.getName()));
        if (annAttrs != null) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MapperProviderConfigurer.class);

            Class<? extends Annotation> annotationClass = annAttrs.getClass("annotationClass");
            if (!Annotation.class.equals(annotationClass)) {
                builder.addPropertyValue("annotationClass", annotationClass);
            }

            Class<?> markerInterface = annAttrs.getClass("markerInterface");
            if (!Class.class.equals(markerInterface)) {
                builder.addPropertyValue("markerInterface", markerInterface);
            }

            String sqlSessionFactoryRef = annAttrs.getString("sqlSessionFactoryRef");
            if (StringUtils.hasText(sqlSessionFactoryRef)) {
                builder.addPropertyValue("sqlSessionFactoryBeanName", annAttrs.getString("sqlSessionFactoryRef"));
            }

            List<String> basePackages = Arrays.stream(annAttrs.getStringArray("value"))
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toList());

            basePackages.addAll(Arrays.stream(annAttrs.getStringArray("basePackages"))
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toList()));

            builder.addPropertyValue("basePackages", basePackages);

            String beanName = importingClassMetadata.getClassName() + "#" + MapperProviderRegistrar.class.getSimpleName() + "#" + 0;

            registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
        }
    }

}
