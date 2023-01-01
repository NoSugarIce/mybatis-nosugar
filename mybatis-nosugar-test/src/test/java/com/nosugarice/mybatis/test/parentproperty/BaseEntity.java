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

package com.nosugarice.mybatis.test.parentproperty;

import com.nosugarice.mybatis.annotation.ColumnOptions;
import com.nosugarice.mybatis.annotation.LogicDelete;
import com.nosugarice.mybatis.test.base.valuehandler.NowHandler;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author dingjingyang@foxmail.com
 * @date 2022/1/15
 */
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = -2355967286317673597L;

    /** 主键 */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "uuid")
    private String id;

    /** 创建时间 */
    @ColumnOptions(insertHandler = NowHandler.class)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @ColumnOptions(updateHandler = NowHandler.class)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** 删除时间 */
    @LogicDelete(deleteValue = "NOW")
    @Column(name = "disabled_at")
    private LocalDateTime disabledAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDisabledAt() {
        return disabledAt;
    }

    public void setDisabledAt(LocalDateTime disabledAt) {
        this.disabledAt = disabledAt;
    }
}
