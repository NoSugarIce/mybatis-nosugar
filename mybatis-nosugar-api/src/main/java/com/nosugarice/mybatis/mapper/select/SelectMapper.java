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

package com.nosugarice.mybatis.mapper.select;

import com.nosugarice.mybatis.annotation.ProviderAdapter;
import com.nosugarice.mybatis.mapper.MapperParam;
import com.nosugarice.mybatis.mapper.function.AdapterMapper;
import com.nosugarice.mybatis.mapper.function.FunS;
import com.nosugarice.mybatis.mapper.function.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author dingjingyang@foxmail.com
 * @date 2019/1/7
 */
public interface SelectMapper extends AdapterMapper, Mapper {

    /**
     * 桥接 COUNT 查询
     *
     * @param funS
     * @param params
     * @return
     */
    @ProviderAdapter(value = ProviderAdapter.Type.COUNT)
    Number adapterCount(@Param(MapperParam.MAPPER_FUNCTION) FunS<?> funS, @Param(MapperParam.PARAMS) Object... params);

    /**
     * 桥接 EXISTS 查询
     *
     * @param funS
     * @param params
     * @return
     */
    @ProviderAdapter(value = ProviderAdapter.Type.EXISTS)
    Optional<Integer> adapterExists(@Param(MapperParam.MAPPER_FUNCTION) FunS<?> funS, @Param(MapperParam.PARAMS) Object... params);

    /**
     * 桥接count 查询
     *
     * @param funS
     * @param params
     * @param <T>
     * @return
     */
    @ProviderAdapter(value = ProviderAdapter.Type.PAGE)
    <T> List<T> adapterPage(@Param(MapperParam.MAPPER_FUNCTION) FunS<?> funS, @Param(MapperParam.PARAMS) Object... params);

}
