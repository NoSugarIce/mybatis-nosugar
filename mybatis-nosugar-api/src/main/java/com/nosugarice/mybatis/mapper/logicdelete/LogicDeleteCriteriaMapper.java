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

package com.nosugarice.mybatis.mapper.logicdelete;

import com.nosugarice.mybatis.criteria.CriteriaDelete;
import com.nosugarice.mybatis.criteria.EntityToCriterion;
import com.nosugarice.mybatis.mapper.delete.DeleteCriteriaMapper;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/9
 */
public interface LogicDeleteCriteriaMapper<T> extends DeleteCriteriaMapper<T>, LogicDeleteMapper {

    /**
     * 根据查询条件删除
     *
     * @param criteria
     * @return
     */
    default <C> int logicDelete(CriteriaDelete<T, C> criteria) {
        return delete(criteria, true);
    }

    /**
     * 根据查询的条件进行删除
     *
     * @param entity
     * @return
     */
    default int logicDelete(T entity) {
        return logicDelete(EntityToCriterion.getInstance().entityToCriteriaDelete(entity));
    }

}
