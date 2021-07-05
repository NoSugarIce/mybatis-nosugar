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

package com.nosugarice.mybatis.config;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/6
 */
public class SwitchConfig {

    /** 开启crud */
    private boolean crud = true;

    /** 开启根据方法名构建查询语句 */
    private boolean findByMethodName = true;

    /** 开启分页等功能 */
    private boolean mutative = true;

    /** 逻辑删除开关 */
    private boolean logicDelete = true;

    /** 乐观锁开关 */
    private boolean version = true;

    /** 懒加载 */
    private boolean lazyBuilder;

    /** 批量增强 */
    private boolean speedBatch = true;

    public boolean isCrud() {
        return crud;
    }

    public void setCrud(boolean crud) {
        this.crud = crud;
    }

    public boolean isFindByMethodName() {
        return findByMethodName;
    }

    public void setFindByMethodName(boolean findByMethodName) {
        this.findByMethodName = findByMethodName;
    }

    public boolean isMutative() {
        return mutative;
    }

    public void setMutative(boolean mutative) {
        this.mutative = mutative;
    }

    public boolean isLogicDelete() {
        return logicDelete;
    }

    public void setLogicDelete(boolean logicDelete) {
        this.logicDelete = logicDelete;
    }

    public boolean isVersion() {
        return version;
    }

    public void setVersion(boolean version) {
        this.version = version;
    }

    public boolean isLazyBuilder() {
        return lazyBuilder;
    }

    public void setLazyBuilder(boolean lazyBuilder) {
        this.lazyBuilder = lazyBuilder;
    }

    public boolean isSpeedBatch() {
        return speedBatch;
    }

    public void setSpeedBatch(boolean speedBatch) {
        this.speedBatch = speedBatch;
    }
}
