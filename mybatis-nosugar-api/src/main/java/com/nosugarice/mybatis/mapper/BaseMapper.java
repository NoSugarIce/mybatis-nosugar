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

package com.nosugarice.mybatis.mapper;

import com.nosugarice.mybatis.mapper.base.BaseDeleteMapper;
import com.nosugarice.mybatis.mapper.base.BaseInsertMapper;
import com.nosugarice.mybatis.mapper.base.BaseLogicDeleteMapper;
import com.nosugarice.mybatis.mapper.base.BaseSelectMapper;
import com.nosugarice.mybatis.mapper.base.BaseUpdateMapper;
import com.nosugarice.mybatis.mapper.function.MethodNameMapper;

import java.io.Serializable;

/**
 * @author dingjingyang@foxmail.com
 * @date 2017/8/29
 */
public interface BaseMapper<T, ID extends Serializable> extends BaseSelectMapper<T, ID>, BaseInsertMapper<T>
        , BaseUpdateMapper<T>, BaseDeleteMapper<T, ID>, BaseLogicDeleteMapper<T, ID>, MethodNameMapper<T> {
}

