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

package com.nosugarice.mybatis.criteria.select;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public enum AggFunction implements SQLFunction {

    /** 返回数值列的平均值,NULL值不包括在计算中 */
    AVG {
        @Override
        public String getName() {
            return "AVG";
        }
    },
    /** 返回匹配指定条件的行数 */
    COUNT {
        @Override
        public String getName() {
            return "COUNT";
        }
    },
    /** 返回一列中的最大值,NULL值不包括在计算中 */
    MAX {
        @Override
        public String getName() {
            return "MAX";
        }
    },
    /** 返回一列中的最小值,NULL值不包括在计算中 */
    MIN {
        @Override
        public String getName() {
            return "MIN";
        }
    },
    /** 返回数值列的总数(总额) */
    SUM {
        @Override
        public String getName() {
            return "SUM";
        }
    }

}
