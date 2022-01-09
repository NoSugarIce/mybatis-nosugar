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

import com.nosugarice.mybatis.domain.Page;
import com.nosugarice.mybatis.mapper.function.FunS;
import com.nosugarice.mybatis.utils.Assert;

import java.util.List;

/**
 * 分页查询
 *
 * @author dingjingyang@foxmail.com
 * @date 2017/8/30
 */
public interface SelectPageMapper<T> extends SelectMapper {

    /**
     * 分页查询
     *
     * @param page           分页参数
     * @param selectFunction 查询方法
     * @return 分页数据
     */
    default Page<T> selectPageP0(Page<T> page, FunS.Param0<List<T>> selectFunction) {
        return selectPage(page, selectFunction);
    }

    /**
     * 分页查询
     *
     * @param page           分页参数
     * @param selectFunction 查询方法
     * @param params         参数1
     * @param <X1>           参数1 类型
     * @return 分页数据
     */
    default <X1> Page<T> selectPageP1(Page<T> page, FunS.Param1<X1, List<T>> selectFunction, X1 params) {
        return selectPage(page, selectFunction, params);
    }

    /**
     * 分页查询
     *
     * @param page           分页参数
     * @param selectFunction 查询方法
     * @param params1        参数1
     * @param params2        参数2
     * @param <X1>           参数1 类型
     * @param <X2>           参数2 类型
     * @return 分页数据
     */
    default <X1, X2> Page<T> selectPageP2(Page<T> page, FunS.Param2<X1, X2, List<T>> selectFunction, X1 params1, X2 params2) {
        return selectPage(page, selectFunction, params1, params2);
    }

    /**
     * 分页查询
     *
     * @param page           分页参数
     * @param selectFunction 查询方法
     * @param params1        参数1
     * @param params2        参数2
     * @param params3        参数3
     * @param <X1>           参数1 类型
     * @param <X2>           参数2 类型
     * @param <X3>           参数3 类型
     * @return 分页数据
     */
    default <X1, X2, X3> Page<T> selectPageP3(Page<T> page, FunS.Param3<X1, X2, X3, List<T>> selectFunction
            , X1 params1, X2 params2, X3 params3) {
        return selectPage(page, selectFunction, params1, params2, params3);
    }

    /**
     * 分页查询
     *
     * @param page           分页参数
     * @param selectFunction 查询方法
     * @param params1        参数1
     * @param params2        参数2
     * @param params3        参数3
     * @param params4        参数4
     * @param <X1>           参数1 类型
     * @param <X2>           参数2 类型
     * @param <X3>           参数3 类型
     * @param <X4>           参数4 类型
     * @return 分页数据
     */
    default <X1, X2, X3, X4> Page<T> selectPageP4(Page<T> page, FunS.Param4<X1, X2, X3, X4, List<T>> selectFunction
            , X1 params1, X2 params2, X3 params3, X4 params4) {
        return selectPage(page, selectFunction, params1, params2, params3);
    }

    /**
     * 分页查询
     *
     * @param page           分页参数
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
     * @return 分页数据
     */
    default <X1, X2, X3, X4, X5> Page<T> selectPageP5(Page<T> page, FunS.Param5<X1, X2, X3, X4, X5, List<T>> selectFunction
            , X1 params1, X2 params2, X3 params3, X4 params4, X5 params5) {
        return selectPage(page, selectFunction, params1, params2, params3);
    }

    /**
     * 分页查询
     * 假装 private
     *
     * @param page           分页参数
     * @param selectFunction 查询方法
     * @param params         参数列表
     * @return 分页数据
     */
    default Page<T> selectPage(Page<T> page, FunS<List<T>> selectFunction, Object... params) {
        Assert.notNull(page, "Page不能为空.");
        long count = page.getTotal() > 0 ? page.getTotal() : adapterCount(selectFunction, params);
        page.setTotal(count);
        if (count > 0 && (long) (page.getNumber() - 1) * page.getSize() < count) {
            try {
                PageStorage.setPage(page);
                List<T> list = adapterPage(selectFunction, params);
                page.setContent(list);
            } finally {
                PageStorage.clean();
            }
        }
        return page;
    }

    class PageStorage {

        private static final ThreadLocal<Page<?>> PAGE_THREAD_LOCAL = new ThreadLocal<>();

        public static Page<?> getPage() {
            return PAGE_THREAD_LOCAL.get();
        }

        private static void setPage(Page<?> page) {
            PAGE_THREAD_LOCAL.set(page);
        }

        private static void clean() {
            PAGE_THREAD_LOCAL.remove();
        }

    }

}
