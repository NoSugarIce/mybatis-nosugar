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

package com.nosugarice.mybatis.test.base.mode;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/1/17
 */
public enum StatusEnum {
    ON(0, "生效"),
    OFF(1, "失效"),
    ;

    private final Integer status;
    private final String des;

    StatusEnum(Integer status, String des) {
        this.status = status;
        this.des = des;
    }

    public static StatusEnum valueOfStatus(Integer status) {
        for (StatusEnum value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDes() {
        return des;
    }
}
