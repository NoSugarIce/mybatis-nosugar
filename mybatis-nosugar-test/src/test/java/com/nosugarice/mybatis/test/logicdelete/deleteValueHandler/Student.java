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

package com.nosugarice.mybatis.test.logicdelete.deleteValueHandler;

import com.nosugarice.mybatis.annotation.LogicDelete;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021-6-8
 */
@Table(name = "student")
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    /** 姓名 */
    @Column(name = "name", nullable = false)
    private String name;

    /** 删除 */
    @LogicDelete(defaultValue = "0", deleteValue = "1", deleteValueHandler = DateValueHandler.class)
    @Column(name = "disabled")
    private Integer disabled;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

}
