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

package com.nosugarice.mybatis.builder.sql;

import com.nosugarice.mybatis.sql.RenderingContext;
import com.nosugarice.mybatis.util.StringUtils;

import java.util.Iterator;
import java.util.Optional;

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
        if (StringUtils.isEmpty(assignJdbcType)) {
            assignJdbcType = Optional.ofNullable(MetadataCache.getByProperty(entityClass, column))
                    .map(MetadataCache.PropertyCache::getAssignJdbcType)
                    .orElse(SqlPart.EMPTY);
        }
        if (StringUtils.isEmpty(assignTypeHandler)) {
            assignTypeHandler = Optional.ofNullable(MetadataCache.getByProperty(entityClass, column))
                    .map(MetadataCache.PropertyCache::getAssignTypeHandler)
                    .orElse(SqlPart.EMPTY);
        }
        return SqlPart.placeholder(paramName, prefix, assignJdbcType, assignTypeHandler);
    }

}
