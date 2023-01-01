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

package com.nosugarice.mybatis.entity;

import com.nosugarice.mybatis.utils.ServiceLoaderUtils;

import java.util.Set;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/9/19
 */
public interface EntityData {

    /**
     * 获取主键值
     *
     * @param entity 实体对象
     * @param <T>    实体类型
     * @param <ID>   id类型
     * @return
     */
    <T, ID> ID getId(T entity);

    /**
     * 根据两个实体对象对比出不同值的属性集合
     *
     * @param sourceEntity 原实体对象
     * @param entity       对比的实体对象
     * @param <T>          实体类型
     * @return
     */
    <T> Set<String> diffValueProperty(T sourceEntity, T entity);

    class Holder {

        private static final EntityData INSTANCE = getEntityData();

        private static EntityData getEntityData() {
            return ServiceLoaderUtils.loadSingleInstance(EntityData.class);
        }

        public static EntityData getInstance() {
            return INSTANCE;
        }

    }

}
