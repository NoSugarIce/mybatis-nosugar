package com.nosugarice.mybatis.criteria.where.criterion;

import com.nosugarice.mybatis.criteria.clause.Operator;
import com.nosugarice.mybatis.sql.SQLConstants;

/**
 * 比较操作符
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public enum OperatorType implements Operator {
    /** 相等 */
    NULL {
        @Override
        public OperatorType negated() {
            return NOT_NULL;
        }

        @Override
        public String operator() {
            return SQLConstants.IS_NULL;
        }
    },
    /** 相等 */
    NOT_NULL {
        @Override
        public OperatorType negated() {
            return NULL;
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
            return SQLConstants.NOT_IN;
        }
    },
    /** 未定义 */
    UNDEFINED {
        @Override
        public OperatorType negated() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String operator() {
            throw new UnsupportedOperationException();
        }
    }
}
