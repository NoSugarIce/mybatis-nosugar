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

import java.util.function.Function;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class Like extends SingleValueCriterion<String, Like> {

    private static final long serialVersionUID = -6810211854915959905L;

    public Like(String column, String value) {
        super(column, value, OperatorType.LIKE);
    }


    public static class StartLike extends Like {

        private static final long serialVersionUID = -2280464375029796120L;

        public static final Function<String, String> MATCH = pattern -> pattern + '%';

        public StartLike(String column) {
            this(column, null);
        }

        public StartLike(String column, String value) {
            super(column, MATCH.apply(value));
        }
    }

    public static class EndLike extends Like {

        private static final long serialVersionUID = 2812781368677440111L;

        public static final Function<String, String> MATCH = pattern -> '%' + pattern;

        public EndLike(String column) {
            this(column, null);
        }

        public EndLike(String column, String value) {
            super(column, MATCH.apply(value));
        }
    }

    public static class AnyLike extends Like {

        private static final long serialVersionUID = -6896746513911588772L;

        public static final Function<String, String> MATCH = pattern -> '%' + pattern + '%';

        public AnyLike(String column) {
            this(column, null);
        }

        public AnyLike(String column, String value) {
            super(column, MATCH.apply(value));
        }
    }

}
