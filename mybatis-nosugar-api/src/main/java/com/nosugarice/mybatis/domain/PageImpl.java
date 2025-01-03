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

import java.util.ArrayList;
import java.util.List;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class PageImpl<T> implements Page<T> {

    private static final long serialVersionUID = -8391021130554734373L;

    /**
     * 当前页
     */
    private int pageNumber = 1;

    /**
     * 每页数据容量
     * 默认 10
     */
    private int pageSize = 10;

    /**
     * 总记录数
     */
    private long total = -1;

    private final List<T> content = new ArrayList<>();

    public PageImpl() {
    }

    public PageImpl(int pageSize) {
        this.pageSize = pageSize;
    }

    public PageImpl(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public PageImpl(int pageNumber, int pageSize, long total, List<T> content) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.total = total;
        this.content.addAll(content);
    }

    @Override
    public int getNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public int getSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public List<T> getContent() {
        return content;
    }

    @Override
    public void setContent(List<T> content) {
        this.content.addAll(content);
    }

    @Override
    public long getTotal() {
        return total;
    }

    @Override
    public void setTotal(long total) {
        this.total = total;
    }

    @Override
    public Page<T> getPageable() {
        return new PageImpl<>(getNumber(), getSize());
    }

    @Override
    public Page<T> nextPageable() {
        return new PageImpl<>(getNumber() + 1, getSize());
    }

}
