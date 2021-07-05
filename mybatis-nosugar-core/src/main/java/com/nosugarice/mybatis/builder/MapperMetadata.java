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

package com.nosugarice.mybatis.builder;

import com.nosugarice.mybatis.config.MapperBuilderConfig;
import com.nosugarice.mybatis.config.Supports;
import com.nosugarice.mybatis.dialect.Dialect;
import com.nosugarice.mybatis.mapping.RelationalEntity;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/2
 */
public class MapperMetadata {

    private final Class<?> mapperInterface;
    private final Dialect dialect;
    private final RelationalEntity relationalEntity;
    private final Supports supports;

    public MapperMetadata(Class<?> mapperInterface, Dialect dialect, RelationalEntity relationalEntity, MapperBuilderConfig config) {
        this.mapperInterface = mapperInterface;
        this.dialect = dialect;
        this.relationalEntity = relationalEntity;
        this.supports = new Supports(relationalEntity, config);
    }

    public Dialect getDialect() {
        return dialect;
    }

    public Class<?> getMapperInterface() {
        return mapperInterface;
    }

    public RelationalEntity getRelationalEntity() {
        return relationalEntity;
    }

    public Supports getSupports() {
        return supports;
    }

}
