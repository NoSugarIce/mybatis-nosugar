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

package com.nosugarice.mybatis.query.criteria.impl;

import com.nosugarice.mybatis.query.criteria.AbstractCriteriaQuery;
import com.nosugarice.mybatis.query.criteria.EntityCriteriaQuery;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public abstract class AbstractEntityCriteriaQuery<T, C> extends AbstractCriteriaQuery<T, C> implements EntityCriteriaQuery<T> {

    private T entity;

    protected AbstractEntityCriteriaQuery(Class<T> entityClass) {
        super(entityClass);
    }

    @SuppressWarnings("unchecked")
    public AbstractEntityCriteriaQuery(T entity) {
        this((Class<T>) entity.getClass());
        this.entity = entity;
    }

    @Override
    public T getEntity() {
        return entity;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

}
