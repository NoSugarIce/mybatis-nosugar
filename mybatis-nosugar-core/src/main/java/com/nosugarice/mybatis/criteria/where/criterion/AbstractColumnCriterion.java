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

package com.nosugarice.mybatis.criteria.where.criterion;

import com.nosugarice.mybatis.criteria.ColumnReader;
import com.nosugarice.mybatis.criteria.where.ColumnCriterion;
import com.nosugarice.mybatis.sql.SQLConstants;
import com.nosugarice.mybatis.sql.SQLPart;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public abstract class AbstractColumnCriterion<T, E extends AbstractColumnCriterion<T, E>> extends AbstractCriterion<E>
        implements ColumnReader, ColumnCriterion<T> {

    private static final long serialVersionUID = -459503157484510248L;

    protected String column;

    protected T value;

    protected boolean not;

    protected AbstractColumnCriterion(String column, T value) {
        this.column = column;
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E not() {
        this.not = !not;
        return (E) this;
    }

    @Override
    public String patternSql(String value) {
        return SQLPart.merge(getConnector(), withTableAliasPlaceholder(), (not ? getOperator().negated() : getOperator()).operator(), value);
    }

    /**
     * 获取比较操作符
     *
     * @return
     */
    protected abstract OperatorType getOperator();

    @Override
    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    @Override
    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public T getSecondValue() {
        return null;
    }

    @Override
    public boolean isNoValue() {
        return false;
    }

    @Override
    public boolean isSingleValue() {
        return false;
    }

    @Override
    public boolean isTwoValue() {
        return false;
    }

    @Override
    public boolean isListValue() {
        return false;
    }

    /**
     * 比较操作符
     */
    protected enum OperatorType implements Operator {
        /** 相等 */
        Null {
            @Override
            public OperatorType negated() {
                return NOT_Null;
            }

            @Override
            public String operator() {
                return SQLConstants.IS_NULL;
            }
        },
        /** 相等 */
        NOT_Null {
            @Override
            public OperatorType negated() {
                return Null;
            }

            @Override
            public String operator() {
                return SQLConstants.IS_NOT_NULL;
            }
        },
        /** 相等 */
        EQUALS_TO {
            @Override
            public OperatorType negated() {
                return NOT_EQUALS_TO;
            }

            @Override
            public String operator() {
                return SQLConstants.EQUALS_TO;
            }
        },
        /** 不相等 */
        NOT_EQUALS_TO {
            @Override
            public OperatorType negated() {
                return EQUALS_TO;
            }

            @Override
            public String operator() {
                return SQLConstants.NOT_EQUALS_TO;
            }
        },
        /** 小于 */
        LESS_THAN {
            @Override
            public OperatorType negated() {
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
            public OperatorType negated() {
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
            public OperatorType negated() {
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
            public OperatorType negated() {
                return LESS_THAN;
            }

            @Override
            public String operator() {
                return ">=";
            }
        },
        /** LIKE */
        LIKE {
            @Override
            public OperatorType negated() {
                return NOT_LIKE;
            }

            @Override
            public String operator() {
                return SQLConstants.LIKE;
            }
        },
        /** NOT_LIKE */
        NOT_LIKE {
            @Override
            public OperatorType negated() {
                return LIKE;
            }

            @Override
            public String operator() {
                return SQLConstants.NOT_LIKE;
            }
        },
        /** 之间 */
        BETWEEN {
            @Override
            public OperatorType negated() {
                return NOT_BETWEEN;
            }

            @Override
            public String operator() {
                return SQLConstants.BETWEEN;
            }
        },
        /** 不在之间 */
        NOT_BETWEEN {
            @Override
            public OperatorType negated() {
                return BETWEEN;
            }

            @Override
            public String operator() {
                return SQLConstants.NOT_BETWEEN;
            }
        },
        /** 匹配多个值 */
        IN {
            @Override
            public OperatorType negated() {
                return NOT_IN;
            }

            @Override
            public String operator() {
                return SQLConstants.IN;
            }
        },
        /** 不匹配多个值 */
        NOT_IN {
            @Override
            public OperatorType negated() {
                return LESS_THAN;
            }

            @Override
            public String operator() {
                return SQLConstants.IN;
            }
        }
    }

}
