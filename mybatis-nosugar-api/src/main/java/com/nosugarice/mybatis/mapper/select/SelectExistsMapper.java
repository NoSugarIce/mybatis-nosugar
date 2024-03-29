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

package com.nosugarice.mybatis.mapper.select;

import com.nosugarice.mybatis.mapper.function.FunS;

import java.util.List;
import java.util.Optional;

/**
 * exists 查询,作为AdapterMapper#adapterExists(function.FunS, Object...) 帮助使用,可以不用方法引用强制转换
 *
 * @author dingjingyang@foxmail.com
 * @date 2017/8/30
 */
public interface SelectExistsMapper<T> extends SelectMapper {

    /**
     * 是否存在查询
     *
     * @param selectFunction 查询方法
     * @return 总数
     */
    default Optional<Integer> existsP0(FunS.Param0<List<T>> selectFunction) {
        return adapterExists(selectFunction);
    }

    /**
     * 是否存在查询
     *
     * @param selectFunction 查询方法
     * @param params         参数1
     * @param <X1>           参数1 类型
     * @return 总数
     */
    default <X1> Optional<Integer> existsP1(FunS.Param1<X1, List<T>> selectFunction, X1 params) {
        return adapterExists(selectFunction, params);
    }

    /**
     * 是否存在查询
     *
     * @param selectFunction 查询方法
     * @param params1        参数1
     * @param params2        参数2
     * @param <X1>           参数1 类型
     * @param <X2>           参数2 类型
     * @return 总数
     */
    default <X1, X2> Optional<Integer> existsP2(FunS.Param2<X1, X2, List<T>> selectFunction, X1 params1, X2 params2) {
        return adapterExists(selectFunction, params1, params2);
    }

    /**
     * 是否存在查询
     *
     * @param selectFunction 查询方法
     * @param params1        参数1
     * @param params2        参数2
     * @param params3        参数3
     * @param <X1>           参数1 类型
     * @param <X2>           参数2 类型
     * @param <X3>           参数3 类型
     * @return 总数
     */
    default <X1, X2, X3> Optional<Integer> existsP3(FunS.Param3<X1, X2, X3, List<T>> selectFunction
            , X1 params1, X2 params2, X3 params3) {
        return adapterExists(selectFunction, params1, params2, params3);
    }

    /**
     * 是否存在查询
     *
     * @param selectFunction 查询方法
     * @param params1        参数1
     * @param params2        参数2
     * @param params3        参数3
     * @param params4        参数4
     * @param <X1>           参数1 类型
     * @param <X2>           参数2 类型
     * @param <X3>           参数3 类型
     * @param <X4>           参数4 类型
     * @return 总数
     */
    default <X1, X2, X3, X4> Optional<Integer> existsP4(FunS.Param4<X1, X2, X3, X4, List<T>> selectFunction
            , X1 params1, X2 params2, X3 params3, X4 params4) {
        return adapterExists(selectFunction, params1, params2, params3, params4);
    }

    /**
     * 是否存在查询
     *
     * @param selectFunction 查询方法
     * @param params1        参数1
     * @param params2        参数2
     * @param params3        参数3
     * @param params4        参数4
     * @param params5        参数5
     * @param <X1>           参数1 类型
     * @param <X2>           参数2 类型
     * @param <X3>           参数3 类型
     * @param <X4>           参数4 类型
     * @param <X5>           参数5 类型
     * @return 总数
     */
    default <X1, X2, X3, X4, X5> Optional<Integer> existsP5(FunS.Param5<X1, X2, X3, X4, X5, List<T>> selectFunction
            , X1 params1, X2 params2, X3 params3, X4 params4, X5 params5) {
        return adapterExists(selectFunction, params1, params2, params3, params4, params5);
    }

}
