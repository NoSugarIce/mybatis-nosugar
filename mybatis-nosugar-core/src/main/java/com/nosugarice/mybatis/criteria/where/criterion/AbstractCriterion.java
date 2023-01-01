/*
 * Copyright 2021-2023 NoSugarIce
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nosugarice.mybatis.criteria.where.criterion;

import com.nosugarice.mybatis.criteria.criterion.Criterion;
import com.nosugarice.mybatis.sql.SQLStrategy;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public abstract class AbstractCriterion<T extends AbstractCriterion<T>> implements Criterion {

    private static final long serialVersionUID = 1L;

    private boolean condition = true;

    protected ConnectorType connectorType;

    private transient SQLStrategy sqlStrategy;

    @Override
    public ConnectorType getConnectorType() {
        return connectorType;
    }

    @Override
    public boolean condition() {
        return condition;
    }

    public void setCondition(boolean condition) {
        this.condition = condition;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T byAnd() {
        this.connectorType = ConnectorType.AND;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T byOr() {
        this.connectorType = ConnectorType.OR;
        return (T) this;
    }

    @Override
    public void setSqlStrategy(SQLStrategy sqlStrategy) {
        this.sqlStrategy = sqlStrategy;
    }

    @Override
    public String getSql() {
        return sqlStrategy.getSql();
    }
}
