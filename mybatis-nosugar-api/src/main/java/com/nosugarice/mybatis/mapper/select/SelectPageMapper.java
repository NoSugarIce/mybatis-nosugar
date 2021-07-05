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
import com.nosugarice.mybatis.domain.Page;
import com.nosugarice.mybatis.mapper.function.FunS;
import org.apache.ibatis.annotations.Param;

import java.util.Arrays;
import java.util.List;

/**
 * 只支持被@Provider(adapter = Provider.Type.COUNT)桥接的方法
 *
 * @author dingjingyang@foxmail.com
 * @date 2017/8/30
 */
public interface SelectPageMapper<T> extends SelectMapper {

    /**
     * 分页查询
     *
     * @param page           分页参数
     * @param selectFunction 查询方法(方法需要@Provider(adapter = Provider.Type.COUNT))
     * @return 分页数据
     */
    default Page<T> selectPageP0(Page<T> page, FunS.Param0<List<T>> selectFunction) {
        return selectPage(page, selectFunction);
    }

    /**
     * 分页查询
     *
     * @param page           分页参数
     * @param selectFunction 查询方法(方法需要@Provider(adapter = Provider.Type.COUNT))
     * @param params         参数1
     * @param <X1>           参数1类型
     * @return 分页数据
     */
    default <X1> Page<T> selectPageP1(Page<T> page, FunS.Param1<X1, List<T>> selectFunction, X1 params) {
        return selectPage(page, selectFunction, params);
    }

    /**
     * 分页查询
     *
     * @param page           分页参数
     * @param selectFunction 查询方法(方法需要@Provider(adapter = Provider.Type.COUNT))
     * @param params1        参数1
     * @param params2        参数2
     * @param <X1>           参数1类型
     * @param <X2>           参数2类型
     * @return 分页数据
     */
    default <X1, X2> Page<T> selectPageP2(Page<T> page, FunS.Param2<X1, X2, List<T>> selectFunction, X1 params1, X2 params2) {
        return selectPage(page, selectFunction, params1, params2);
    }

    /**
     * 分页查询
     *
     * @param page           分页参数
     * @param selectFunction 查询方法(方法需要@Provider(adapter = Provider.Type.COUNT))
     * @param params1        参数1
     * @param params2        参数2
     * @param params3        参数3
     * @param <X1>           参数1类型
     * @param <X2>           参数2类型
     * @param <X3>           参数3类型
     * @return 分页数据
     */
    default <X1, X2, X3> Page<T> selectPageP3(Page<T> page, FunS.Param3<X1, X2, X3, List<T>> selectFunction
            , X1 params1, X2 params2, X3 params3) {
        return selectPage(page, selectFunction, params1, params2, params3);
    }

    /**
     * 分页查询
     *
     * @param <X1>           参数1类型
     * @param <X2>           参数2类型
     * @param <X3>           参数3类型
     * @param page           分页参数
     * @param selectFunction 查询方法(方法需要@Provider(adapter = Provider.Type.COUNT))
     * @param params1        参数1
     * @param params2        参数2
     * @param params3        参数3
     * @param params4        参数4
     * @return 分页数据
     */
    default <X1, X2, X3, X4> Page<T> selectPageP4(Page<T> page, FunS.Param4<X1, X2, X3, X4, List<T>> selectFunction
            , X1 params1, X2 params2, X3 params3, X4 params4) {
        return selectPage(page, selectFunction, params1, params2, params3);
    }

    /**
     * 分页查询
     *
     * @param <X1>           参数1类型
     * @param <X2>           参数2类型
     * @param <X3>           参数3类型
     * @param page           分页参数
     * @param selectFunction 查询方法(方法需要@Provider(adapter = Provider.Type.COUNT))
     * @param params1        参数1
     * @param params2        参数2
     * @param params3        参数3
     * @param params4        参数4
     * @param params5        参数5
     * @return 分页数据
     */
    default <X1, X2, X3, X4, X5> Page<T> selectPageP5(Page<T> page, FunS.Param5<X1, X2, X3, X4, X5, List<T>> selectFunction
            , X1 params1, X2 params2, X3 params3, X4 params4, X5 params5) {
        return selectPage(page, selectFunction, params1, params2, params3);
    }

    /**
     * 分页查询
     *
     * @param page           分页参数
     * @param selectFunction 查询方法(方法需要@Provider(adapter = Provider.Type.COUNT))
     * @param params         参数列表
     * @return 分页数据
     */
    default Page<T> selectPage(Page<T> page, FunS<List<T>> selectFunction, Object... params) {
        long count = page.getTotal() > 0 ? page.getTotal() : selectAdapter(selectFunction, params);
        if (count > 0 && (long) (page.getPageNumber() - 1) * page.getPageSize() < count) {
            Object[] paramsArr = Arrays.copyOf(params, params.length + 1);
            paramsArr[params.length] = page;
            try {
                PageTempStorage.setPage(page);
                List<T> list = selectFunction.invoke(paramsArr);
                page.setContent(list);
                page.setTotal(count);
            } finally {
                PageTempStorage.clean();
            }
        }
        return page;
    }

    /**
     * 桥接方法
     *
     * @param funS   桥接方法lambda引用
     * @param params 参数列表
     * @param <R>    返回类型
     * @return 返回数据
     */
    @ProviderAdapter
    <R> R selectAdapter(@Param("mapperBiFunction") FunS<?> funS, @Param("params") Object... params);

}
