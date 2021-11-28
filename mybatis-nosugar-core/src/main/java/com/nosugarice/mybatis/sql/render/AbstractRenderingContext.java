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

package com.nosugarice.mybatis.sql.render;

import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.registry.EntityMetadataRegistry;
import com.nosugarice.mybatis.sql.RenderingContext;
import com.nosugarice.mybatis.sql.SqlPart;
import com.nosugarice.mybatis.util.StringUtils;

import java.util.Iterator;
import java.util.Optional;

import static com.nosugarice.mybatis.sql.SQLConstants.EMPTY;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public abstract class AbstractRenderingContext implements RenderingContext {

    protected final Class<?> entityClass;
    protected final Iterator<String> paramNameIterator;

    protected AbstractRenderingContext(Class<?> entityClass, Iterator<String> paramNameIterator) {
        this.entityClass = entityClass;
        this.paramNameIterator = paramNameIterator;
    }

    public String getPlaceholder(String column, String paramName, String prefix, String assignJdbcType, String assignTypeHandler) {
        if (StringUtils.isEmpty(assignJdbcType) || StringUtils.isEmpty(assignTypeHandler)) {
            Optional<RelationalProperty> propertyOptional = Optional.of(EntityMetadataRegistry.Holder.getInstance())
                    .map(entityMetadataRegistry -> entityMetadataRegistry.getPropertyByColumn(entityClass, column));
            if (StringUtils.isEmpty(assignJdbcType)) {
                assignJdbcType = propertyOptional.map(property -> SqlPart.assignJdbcType(property.getJdbcType()))
                        .orElse(EMPTY);
            }
            if (StringUtils.isEmpty(assignTypeHandler)) {
                assignTypeHandler = propertyOptional.map(property -> SqlPart.assignTypeHandler(property.getTypeHandler()))
                        .orElse(EMPTY);
            }
        }
        return SqlPart.placeholder(paramName, prefix, assignJdbcType, assignTypeHandler);
    }

}
