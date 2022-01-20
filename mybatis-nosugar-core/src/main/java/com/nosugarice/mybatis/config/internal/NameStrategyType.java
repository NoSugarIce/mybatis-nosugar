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

package com.nosugarice.mybatis.config.internal;

import com.nosugarice.mybatis.support.NameStrategy;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/11/15
 */
public enum NameStrategyType implements NameStrategy {

    /** 不做处理 */
    ORIGINAL {
        @Override
        public String conversion(String originalStr) {
            return originalStr;
        }
    },

    /** 下划线转驼峰 */
    UNDERSCORE_TO_CAMEL {
        @Override
        public String conversion(String originalStr) {
            if (originalStr == null || "".equals(originalStr.trim())) {
                return "";
            }
            char[] nameItems = originalStr.toCharArray();
            //去除非字母或数字或下划线的字符
            for (int i = 0; i < nameItems.length; i++) {
                if (!(Character.isLetterOrDigit(nameItems[i]) || UNDERSCORE.equals(String.valueOf(nameItems[i])))) {
                    nameItems[i] = '_';
                }
            }
            originalStr = String.valueOf(nameItems).trim().toLowerCase();
            StringBuilder aliasBuilder = new StringBuilder();
            if (originalStr.contains(UNDERSCORE)) {
                String[] temps = originalStr.split(UNDERSCORE);
                for (String columnSplit : temps) {
                    if (aliasBuilder.length() == 0) {
                        aliasBuilder.append(columnSplit.toLowerCase());
                    } else {
                        aliasBuilder.append(columnSplit.substring(0, 1).toUpperCase()).append(columnSplit.substring(1).toLowerCase());
                    }
                }
            } else {
                aliasBuilder.append(originalStr.substring(0, 1).toLowerCase()).append(originalStr.substring(1));
            }
            return aliasBuilder.toString();
        }
    },

    /** 驼峰转下划线 */
    CAMEL_TO_UNDERSCORE {
        @Override
        public String conversion(String originalStr) {
            if (originalStr == null || "".equals(originalStr.trim())) {
                return "";
            }
            int len = originalStr.length();
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i++) {
                char c = originalStr.charAt(i);
                if (Character.isUpperCase(c)) {
                    sb.append(UNDERSCORE);
                    sb.append(Character.toLowerCase(c));
                } else {
                    sb.append(c);
                }
            }
            return sb.charAt(0) == UNDERSCORE_CHAR ? sb.substring(1) : sb.toString();
        }
    },

    /** 驼峰转下划线大写 */
    CAMEL_TO_UNDERSCORE_UPPERCASE {
        @Override
        public String conversion(String originalStr) {
            return CAMEL_TO_UNDERSCORE.conversion(originalStr).toUpperCase();
        }
    },

    /** 首字母小写 */
    LOWERCASE_FIRST {
        @Override
        public String conversion(String originalStr) {
            if (originalStr == null || "".equals(originalStr.trim())) {
                return "";
            }
            if (Character.isLowerCase(originalStr.charAt(0))) {
                return originalStr;
            } else {
                return originalStr.length() > 1
                        ? Character.toLowerCase(originalStr.charAt(0)) + originalStr.substring(1)
                        : String.valueOf(Character.toLowerCase(originalStr.charAt(0)));
            }

        }
    },

    /** 首字母大写 */
    UPPERCASE_FIRST {
        @Override
        public String conversion(String originalStr) {
            if (originalStr == null || "".equals(originalStr.trim())) {
                return "";
            }
            if (Character.isUpperCase(originalStr.charAt(0))) {
                return originalStr;
            } else {
                return Character.toUpperCase(originalStr.charAt(0)) + originalStr.substring(1);
            }
        }
    },

    /** 下划线或驼峰首字母组合 */
    FIRST_COMBINE {
        @Override
        public String conversion(String originalStr) {
            if (originalStr == null || "".equals(originalStr.trim())) {
                return "";
            }
            StringBuilder aliasBuilder = new StringBuilder();
            if (originalStr.contains(UNDERSCORE)) {
                String[] temps = originalStr.split(UNDERSCORE);
                for (String temp : temps) {
                    aliasBuilder.append(temp.charAt(0));
                }
            } else {
                aliasBuilder.append(originalStr.charAt(0));
                for (int i = 1; i < originalStr.length(); i++) {
                    if (Character.isUpperCase(originalStr.charAt(i))) {
                        aliasBuilder.append(originalStr.charAt(i));
                    }
                }
            }
            return aliasBuilder.toString().toLowerCase();
        }
    };

    private static final String UNDERSCORE = "_";
    private static final char UNDERSCORE_CHAR = '_';

}


