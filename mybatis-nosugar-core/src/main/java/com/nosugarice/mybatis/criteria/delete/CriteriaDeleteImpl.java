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

package com.nosugarice.mybatis.criteria.delete;

import com.nosugarice.mybatis.criteria.CriteriaDelete;
import com.nosugarice.mybatis.criteria.tocolumn.ToColumn;
import com.nosugarice.mybatis.criteria.where.AbstractWhere;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/12/5
 */
public class CriteriaDeleteImpl<T, C> extends AbstractWhere<C, CriteriaDelete<T, C>> implements CriteriaDelete<T, C> {

    private static final long serialVersionUID = -3286450766474934503L;

    public CriteriaDeleteImpl(Class<T> entityClass, ToColumn<C> toColumn) {
        super(entityClass, toColumn);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getType() {
        return (Class<T>) getEntityClass();
    }
}
