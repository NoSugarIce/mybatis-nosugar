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

package com.nosugarice.mybatis.query.criterion;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class Comparison<T, E extends Comparison<T, E>> extends SingleValueCriterion<T, E> {

    private static final long serialVersionUID = 133665460821776142L;

    private final ComparisonOperator comparisonOperator;

    public Comparison(String column, T value, ComparisonOperator comparisonOperator) {
        this(column, value, comparisonOperator, Separator.AND);
    }

    public Comparison(String column, T value, ComparisonOperator comparisonOperator, Separator separator) {
        super(column, comparisonOperator.operator(), value, separator);
        this.comparisonOperator = comparisonOperator;
    }

    @Override
    public boolean isSingleValue() {
        return true;
    }

    @Override
    public String getOperator(boolean negated) {
        return negated ? comparisonOperator.negated().operator() : comparisonOperator.operator();
    }

    /**
     * 比较操作符
     */
    public enum ComparisonOperator {
        /** 相等 */
        EQUAL {
            @Override
            public ComparisonOperator negated() {
                return NOT_EQUAL;
            }

            @Override
            public String operator() {
                return "=";
            }
        },
        /** 不相等 */
        NOT_EQUAL {
            @Override
            public ComparisonOperator negated() {
                return EQUAL;
            }

            @Override
            public String operator() {
                return "<>";
            }
        },
        /** 小于 */
        LESS_THAN {
            @Override
            public ComparisonOperator negated() {
                return GREATER_THAN_OR_EQUAL;
            }

            @Override
            public String operator() {
                return "<";
            }
        },
        /** 小于等于 */
        LESS_THAN_OR_EQUAL {
            @Override
            public ComparisonOperator negated() {
                return GREATER_THAN;
            }

            @Override
            public String operator() {
                return "<=";
            }
        },
        /** 大于 */
        GREATER_THAN {
            @Override
            public ComparisonOperator negated() {
                return LESS_THAN_OR_EQUAL;
            }

            @Override
            public String operator() {
                return ">";
            }
        },
        /** 大于等于 */
        GREATER_THAN_OR_EQUAL {
            @Override
            public ComparisonOperator negated() {
                return LESS_THAN;
            }

            @Override
            public String operator() {
                return ">=";
            }
        };

        public abstract ComparisonOperator negated();

        public abstract String operator();
    }

}
