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

import com.nosugarice.mybatis.annotation.Provider;
import com.nosugarice.mybatis.annotation.ProviderAdapter;
import com.nosugarice.mybatis.mapper.MapperParam;
import com.nosugarice.mybatis.mapper.function.FunS;
import com.nosugarice.mybatis.mapper.function.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author dingjingyang@foxmail.com
 * @date 2019/1/7
 */
public interface SelectMapper extends Mapper {

    /**
     * 桥接方法
     *
     * @param adapter 桥接类型
     * @param funS    桥接方法lambda引用
     * @param params  参数列表
     * @param <R>     返回类型
     * @return 返回数据
     */
    @ProviderAdapter
    <R> R selectAdapter(Provider.Adapter adapter, @Param(MapperParam.MAPPER_FUNCTION) FunS<?> funS, @Param(MapperParam.PARAMS) Object... params);

}
