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

package com.nosugarice.mybatis.mapper.function;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/6/12
 */
public interface MapperService {

    /**
     * 从Mapper 接口获取实体类
     *
     * @param mapperInterface
     * @return
     */
    Class<?> analyzeEntityClass(Class<?> mapperInterface);

    /**
     * 主键栏位是否都不为空
     *
     * @param entity
     * @param <T>
     * @return
     */
    <T> boolean entityIsNew(T entity);

}
