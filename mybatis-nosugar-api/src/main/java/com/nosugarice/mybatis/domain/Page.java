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

package com.nosugarice.mybatis.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @author dingjingyang@foxmail.com
 * @date 2017/6/20
 */
public interface Page<T> extends Serializable {

    /**
     * 获取起始行
     *
     * @return
     */
    default int getOffset() {
        return (getNumber() - 1) * getSize();
    }

    /**
     * 获取获取条数
     *
     * @return
     */
    default int getLimit() {
        return getSize();
    }

    /**
     * 获取当前页数
     *
     * @return
     */
    int getNumber();

    /**
     * 获取页大小
     *
     * @return
     */
    int getSize();

    /**
     * 获取页总数
     *
     * @return
     */
    default int getTotalPages() {
        return getTotal() > 0 ? (int) Math.ceil((double) getTotal() / (double) getSize()) : 1;
    }

    /**
     * 获取数据
     *
     * @return
     */
    List<T> getContent();

    /**
     * 设置当前页内容
     *
     * @param content
     */
    void setContent(List<T> content);

    /**
     * 获取总数据数
     *
     * @return
     */
    long getTotal();

    /**
     * 设置总记录数
     *
     * @param total
     */
    void setTotal(long total);

    /**
     * 当前页的元素数
     *
     * @return
     */
    default int getNumberOfElements() {
        return getContent() == null ? 0 : getContent().size();
    }

    /**
     * 当前页是否存在内容
     *
     * @return
     */
    default boolean hasContent() {
        return getContent() != null && !getContent().isEmpty();
    }

    /**
     * 是否为第一页
     *
     * @return
     */
    default boolean isFirst() {
        return getNumber() == 1;
    }

    /**
     * 是否为最后一页
     *
     * @return
     */
    default boolean isLast() {
        return !hasNext();
    }

    /**
     * 是否存在下一页
     *
     * @return
     */
    default boolean hasNext() {
        return getNumber() + 1 <= getTotalPages();
    }

    /**
     * 是否存在上一页
     *
     * @return
     */
    default boolean hasPrevious() {
        return getNumber() > 1;
    }

    /**
     * 当前页参数
     *
     * @return
     */
    Page<T> getPageable();

    /**
     * 下一页参数
     *
     * @return
     */
    Page<T> nextPageable();

}
