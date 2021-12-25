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

package com.nosugarice.mybatis.criteria.update;

import com.nosugarice.mybatis.criteria.CriteriaUpdate;
import com.nosugarice.mybatis.criteria.ToColumn;
import com.nosugarice.mybatis.criteria.where.AbstractWhere;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/12/5
 */
public class CriteriaUpdateImpl<T, C> extends AbstractWhere<C, CriteriaUpdate<T, C>> implements CriteriaUpdate<T, C>, UpdateStructure {

    private final Map<String, Object> values = new LinkedHashMap<>();

    public CriteriaUpdateImpl(Class<T> entityClass, ToColumn<C> toColumn) {
        super(entityClass, toColumn);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getType() {
        return (Class<T>) getEntityClass();
    }

    @Override
    public CriteriaUpdate<T, C> set(C column, Object value) {
        values.put(toColumn(column), value);
        return this;
    }

    @Override
    public CriteriaUpdate<T, C> setByColumn(String column, Object value) {
        values.put(column, value);
        return this;
    }

    @Override
    public CriteriaUpdate<T, C> cleanValues() {
        values.clear();
        return this;
    }

    @Override
    public Map<String, Object> getSetValues() {
        return values;
    }

}
