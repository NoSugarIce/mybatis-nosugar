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

import com.nosugarice.mybatis.mapper.function.FunS;

import java.util.List;

/**
 * count 查询,作为AdapterMapper#adapterCount(function.FunS, Object...) 帮助使用,可以不用方法引用强制转换
 *
 * @author dingjingyang@foxmail.com
 * @date 2017/8/30
 */
public interface SelectCountMapper<T> extends SelectMapper {

    /**
     * 总数查询
     *
     * @param selectFunction 查询方法
     * @return 总数
     */
    default long countP0(FunS.Param0<List<T>> selectFunction) {
        return adapterCount(selectFunction);
    }

    /**
     * 总数查询
     *
     * @param selectFunction 查询方法
     * @param params         参数1
     * @param <X1>           参数1 类型
     * @return 总数
     */
    default <X1> long countP1(FunS.Param1<X1, List<T>> selectFunction, X1 params) {
        return adapterCount(selectFunction, params);
    }

    /**
     * 总数查询
     *
     * @param selectFunction 查询方法
     * @param params1        参数1
     * @param params2        参数2
     * @param <X1>           参数1 类型
     * @param <X2>           参数2 类型
     * @return 总数
     */
    default <X1, X2> long countP2(FunS.Param2<X1, X2, List<T>> selectFunction, X1 params1, X2 params2) {
        return adapterCount(selectFunction, params1, params2);
    }

    /**
     * 总数查询
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
    default <X1, X2, X3> long countP3(FunS.Param3<X1, X2, X3, List<T>> selectFunction
            , X1 params1, X2 params2, X3 params3) {
        return adapterCount(selectFunction, params1, params2, params3);
    }

    /**
     * 总数查询
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
    default <X1, X2, X3, X4> long countP4(FunS.Param4<X1, X2, X3, X4, List<T>> selectFunction
            , X1 params1, X2 params2, X3 params3, X4 params4) {
        return adapterCount(selectFunction, params1, params2, params3, params4);
    }

    /**
     * 总数查询
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
    default <X1, X2, X3, X4, X5> long countP5(FunS.Param5<X1, X2, X3, X4, X5, List<T>> selectFunction
            , X1 params1, X2 params2, X3 params3, X4 params4, X5 params5) {
        return adapterCount(selectFunction, params1, params2, params3, params4, params5);
    }

}
