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

package com.nosugarice.mybatis.query;

import com.nosugarice.mybatis.builder.sql.AbstractRenderingContext;
import com.nosugarice.mybatis.builder.sql.SqlPart;
import com.nosugarice.mybatis.mapper.function.CriteriaMapper;
import com.nosugarice.mybatis.query.criterion.ListValueCriterion;
import com.nosugarice.mybatis.sql.RenderingContext;
import com.nosugarice.mybatis.sql.criterion.PropertyCriterion;
import com.nosugarice.mybatis.sql.criterion.PropertyCriterionVisitor;
import com.nosugarice.mybatis.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class DefaultRenderPlaceholderVisitor implements PropertyCriterionVisitor<String> {

    private static final String CRITERION_PARAMETER_PREFIX = "p";

    private final Class<?> entityClass;
    private final Map<String, Object> parameters;
    private final AtomicInteger seq = new AtomicInteger();

    public DefaultRenderPlaceholderVisitor(Class<?> entityClass, Map<String, Object> parameters) {
        this.entityClass = entityClass;
        this.parameters = parameters;
    }

    @Override
    public String visit(PropertyCriterion<?> propertyCriterion) {
        List<String> paramKeys = new ArrayList<>();
        if (propertyCriterion.isSingleValue()) {
            String paramKey1 = CRITERION_PARAMETER_PREFIX + seq.getAndIncrement();
            parameters.put(paramKey1, propertyCriterion.getValue());
            paramKeys.add(paramKey1);
        }
        if (propertyCriterion.isTwoValue()) {
            String paramKey1 = CRITERION_PARAMETER_PREFIX + seq.getAndIncrement();
            String paramKey2 = CRITERION_PARAMETER_PREFIX + seq.getAndIncrement();
            parameters.put(paramKey1, propertyCriterion.getValue());
            parameters.put(paramKey2, propertyCriterion.getSecondValue());
            paramKeys.add(paramKey1);
            paramKeys.add(paramKey2);
        }
        if (propertyCriterion.isListValue()) {
            ListValueCriterion<?, ?> listValueCriterion = (ListValueCriterion<?, ?>) propertyCriterion;
            if (CollectionUtils.isEmpty(listValueCriterion.getValues())) {
                return null;
            }
            Collection<?> values = listValueCriterion.getValues();
            for (Object value : values) {
                String param = CRITERION_PARAMETER_PREFIX + seq.getAndIncrement();
                parameters.put(param, value);
                paramKeys.add(param);
            }
        }
        RenderingContext renderingContext = new CriterionRenderingContext(entityClass, paramKeys.iterator());
        return propertyCriterion.renderPlaceholder(renderingContext);
    }

    private static class CriterionRenderingContext extends AbstractRenderingContext {

        private static final String PREFIX = CriteriaMapper.CRITERIA + ".criterionParameter";

        protected CriterionRenderingContext(Class<?> entityClass, Iterator<String> paramNameIterator) {
            super(entityClass, paramNameIterator);
        }

        @Override
        public String getSingleValuePlaceholder(String column) {
            if (paramNameIterator.hasNext()) {
                String paramName = paramNameIterator.next();
                paramNameIterator.remove();
                return getPlaceholder(column, paramName, PREFIX, null, null);
            }
            return SqlPart.EMPTY;
        }

        @Override
        public String getTwoValuePlaceholder(String column) {
            return getSingleValuePlaceholder(column) + SqlPart.AND + getSingleValuePlaceholder(column);
        }

        @Override
        public String getListValuePlaceholder(String column) {
            StringJoiner stringJoiner = new StringJoiner(", ", "(", ")");
            while (paramNameIterator.hasNext()) {
                String paramName = paramNameIterator.next();
                paramNameIterator.remove();
                stringJoiner.add(getPlaceholder(column, paramName, PREFIX, null, null));
            }
            return stringJoiner.toString();
        }
    }

}
