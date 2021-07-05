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

package com.nosugarice.mybatis.sql.criteria;

import java.util.Map;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/4
 */
public interface EntityCriteriaQuery<T> {

    /**
     * 获取实体对象
     *
     * @return
     */
    T getEntity();

    /**
     * 是否简单查询
     *
     * @return
     */
    boolean isSimple();

    /**
     * 是否去重
     *
     * @return
     */
    boolean isDistinct();

    /**
     * 是否存在选择的列
     *
     * @return
     */
    boolean isChooseResult();

    /**
     * 获取选择列
     *
     * @return
     */
    String getChooseResultSql();

    /**
     * 获取扩展列
     *
     * @return
     */
    String getExpandColumnSql();

    /**
     * 获取Criterion 参数
     *
     * @return
     */
    Map<String, Object> getCriterionParameter();

    /**
     * 获取组条件的sql
     *
     * @return
     */
    String getCriterionSql();

    /**
     * 获取 GROUP 语句
     *
     * @return
     */
    String getGroupSql();

    /**
     * 获取 HAVING 语句
     *
     * @return
     */
    String getHavingSql();

    /**
     * 获取 ORDER BY 语句
     *
     * @return
     */
    String getSortSql();

}
