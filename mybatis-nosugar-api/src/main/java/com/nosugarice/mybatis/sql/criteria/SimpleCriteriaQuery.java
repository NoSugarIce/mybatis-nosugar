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

package com.nosugarice.mybatis.sql.criteria;

import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class SimpleCriteriaQuery<T> implements EntityCriteriaQuery<T> {

    private final T entity;

    public SimpleCriteriaQuery(T entity) {
        this.entity = entity;
    }

    @Override
    public T getEntity() {
        return entity;
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    @Override
    public boolean isDistinct() {
        return false;
    }

    @Override
    public boolean isChooseResult() {
        return false;
    }

    @Override
    public String getChooseResultSql() {
        return null;
    }

    @Override
    public String getExpandColumnSql() {
        return null;
    }

    @Override
    public Map<String, Object> getCriterionParameter() {
        return null;
    }

    @Override
    public String getCriterionSql() {
        return null;
    }

    @Override
    public String getGroupSql() {
        return null;
    }

    @Override
    public String getHavingSql() {
        return null;
    }

    @Override
    public String getSortSql() {
        return null;
    }

}
