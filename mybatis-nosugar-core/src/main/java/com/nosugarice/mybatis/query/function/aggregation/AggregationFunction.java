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

package com.nosugarice.mybatis.query.function.aggregation;

import com.nosugarice.mybatis.builder.sql.SqlPart;
import com.nosugarice.mybatis.query.function.FunctionExpression;
import com.nosugarice.mybatis.util.StringUtils;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public abstract class AggregationFunction<T extends AggregationFunction<T>> implements FunctionExpression<AggregationFunction<T>> {

    private static final long serialVersionUID = 7181635081652689329L;

    /** 聚合函数方法名 */
    private final Aggregation aggregation;

    /** 列名 */
    private final String column;

    /** 别名 */
    private String alias;

    public AggregationFunction(Aggregation aggregation, String column) {
        this.aggregation = aggregation;
        this.column = column;
    }

    public AggregationFunction(Aggregation aggregation, String column, String alias) {
        this.aggregation = aggregation;
        this.column = column;
        this.alias = alias;
    }

    @Override
    public String getSql() {
        return getFunctionName() + "(" + getColumn() + ")" + (StringUtils.isEmpty(alias) ? ""
                : (SqlPart.AS + "\"" + getAlias() + "\""));
    }

    @Override
    public String getFunctionName() {
        return aggregation.name();
    }

    public Aggregation getAggregation() {
        return aggregation;
    }

    public String getColumn() {
        return column;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T as(String alias) {
        setAlias(alias);
        return (T) this;
    }

    public enum Aggregation {
        /** 返回数值列的平均值,NULL值不包括在计算中 */
        AVG,
        /** 返回匹配指定条件的行数 */
        COUNT,
        /** 返回一列中的最大值,NULL值不包括在计算中 */
        MAX,
        /** 返回一列中的最小值,NULL值不包括在计算中 */
        MIN,
        /** 返回数值列的总数(总额) */
        SUM
    }

}
