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

package com.nosugarice.mybatis.criteria;

import com.nosugarice.mybatis.criteria.clause.ForUpdate;
import com.nosugarice.mybatis.criteria.clause.From;
import com.nosugarice.mybatis.criteria.clause.GroupBy;
import com.nosugarice.mybatis.criteria.clause.Having;
import com.nosugarice.mybatis.criteria.clause.Join;
import com.nosugarice.mybatis.criteria.clause.Limit;
import com.nosugarice.mybatis.criteria.clause.OrderBy;
import com.nosugarice.mybatis.criteria.clause.Select;
import com.nosugarice.mybatis.criteria.clause.Where;
import com.nosugarice.mybatis.criteria.tocolumn.ToColumn;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public interface Query<T, C, X extends Query<T, C, X>> extends
        Select<C, X>
        , From<T>
        , Join<C, X>
        , Where<C, X>
        , GroupBy<C, X>
        , Having<C, X>
        , OrderBy<C, X>
        , Limit<X>
        , ForUpdate<X> {

    /**
     * 获取列名转换
     *
     * @return
     */
    ToColumn<C> getToColumn();

}
