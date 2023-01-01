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

package com.nosugarice.mybatis.criteria.criterion;

import com.nosugarice.mybatis.sql.SQLStrategy;

/**
 * 查询条件
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public interface Criterion extends SQLStrategy {

    /**
     * 接收访问
     *
     * @param visitor
     * @param <S>
     * @return
     */
    <S> S accept(CriterionSQLVisitor<S> visitor);

    /**
     * 是否添加到条件列表
     *
     * @return
     */
    boolean condition();

    /**
     * 获取链接方式
     *
     * @return
     */
    ConnectorType getConnectorType();

    /**
     * 获取连接符
     *
     * @return 连接符
     */
    default String getConnector() {
        return (getConnectorType() == null ? ConnectorType.AND : getConnectorType()).name();
    }

    /**
     * 设置AND连接符
     *
     * @param <T>
     * @return
     */
    <T extends Criterion> T byAnd();

    /**
     * 设置OR连接符
     *
     * @param <T>
     * @return
     */
    <T extends Criterion> T byOr();


    void setSqlStrategy(SQLStrategy sqlStrategy);

    /** 条件连接符 */
    enum ConnectorType {
        AND, OR
    }

}
