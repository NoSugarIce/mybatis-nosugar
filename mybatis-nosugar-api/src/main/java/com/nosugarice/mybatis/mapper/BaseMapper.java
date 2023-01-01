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

package com.nosugarice.mybatis.mapper;

import com.nosugarice.mybatis.mapper.delete.DeleteCriteriaMapper;
import com.nosugarice.mybatis.mapper.delete.DeletePrimaryKeyMapper;
import com.nosugarice.mybatis.mapper.function.JpaMapper;
import com.nosugarice.mybatis.mapper.insert.InsertMapper;
import com.nosugarice.mybatis.mapper.save.SaveMapper;
import com.nosugarice.mybatis.mapper.select.SelectCountMapper;
import com.nosugarice.mybatis.mapper.select.SelectCriteriaMapper;
import com.nosugarice.mybatis.mapper.select.SelectExistsMapper;
import com.nosugarice.mybatis.mapper.select.SelectPageMapper;
import com.nosugarice.mybatis.mapper.select.SelectPrimaryKeyMapper;
import com.nosugarice.mybatis.mapper.update.UpdateCriteriaMapper;
import com.nosugarice.mybatis.mapper.update.UpdatePrimaryKeyMapper;

import java.io.Serializable;

/**
 * @author dingjingyang@foxmail.com
 * @date 2017/8/29
 */
public interface BaseMapper<T, ID extends Serializable> extends
        SelectPrimaryKeyMapper<T, ID>, SelectCriteriaMapper<T>, SelectPageMapper<T>, SelectCountMapper<T>, SelectExistsMapper<T>
        , InsertMapper<T>
        , UpdatePrimaryKeyMapper<T>, UpdateCriteriaMapper<T>
        , SaveMapper<T, ID>
        , DeletePrimaryKeyMapper<ID>, DeleteCriteriaMapper<T>
        , JpaMapper<T, ID> {
}

