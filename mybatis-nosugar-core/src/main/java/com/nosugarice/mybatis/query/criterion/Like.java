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

import static com.nosugarice.mybatis.sql.SQLConstants.LIKE;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class Like extends SingleValueCriterion<String, Like> {

    private static final long serialVersionUID = -6810211854915959905L;

    public Like(String column, String value, MatchMode matchMode) {
        super(column, LIKE, matchMode.toMatchString(value));
    }

    public enum MatchMode {

        /**
         * Match the entire string to the pattern
         */
        EXACT {
            @Override
            public String toMatchString(String pattern) {
                return pattern;
            }
        },

        /**
         * Match the start of the string to the pattern
         */
        START {
            @Override
            public String toMatchString(String pattern) {
                return pattern + '%';
            }
        },

        /**
         * Match the end of the string to the pattern
         */
        END {
            @Override
            public String toMatchString(String pattern) {
                return '%' + pattern;
            }
        },

        /**
         * Match the pattern anywhere in the string
         */
        ANYWHERE {
            @Override
            public String toMatchString(String pattern) {
                return '%' + pattern + '%';
            }
        };

        /**
         * Convert the pattern, by appending/prepending "%"
         *
         * @param pattern The pattern for convert according to the mode
         * @return The converted pattern
         */
        public abstract String toMatchString(String pattern);

    }
}
