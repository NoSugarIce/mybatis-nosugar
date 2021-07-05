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

package com.nosugarice.mybatis.query.criteria;

import com.nosugarice.mybatis.query.process.Group;

import java.util.Arrays;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public interface GroupBy<T, C> extends ConvertToColumn<C> {

    /**
     * 分组
     *
     * @param groupBy
     * @return
     */
    CriteriaQuery<T, C> groupBy(Group groupBy);

    /**
     * 分组
     *
     * @param columns 列
     * @return
     */
    default CriteriaQuery<T, C> groupBy(C... columns) {
        String[] newColumns = Arrays.stream(columns)
                .map(convert())
                .toArray(String[]::new);
        return groupBy(new Group(newColumns));
    }

}
