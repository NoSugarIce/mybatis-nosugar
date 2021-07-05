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
import com.nosugarice.mybatis.domain.PageImpl;
import com.nosugarice.mybatis.sql.criteria.EntityCriteriaQuery;
import com.nosugarice.mybatis.sql.criteria.SimpleCriteriaQuery;
import org.apache.ibatis.exceptions.TooManyResultsException;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author dingjingyang@foxmail.com
 * @date 2017/8/30
 */
public interface DefaultSelectByCriteriaMapper<T> extends SelectByCriteriaMapper<T> {

    /**
     * 判断是否存在
     *
     * @param entity 实体参数
     * @return 是否存在
     */
    default boolean exists(T entity) {
        return count(entity) > 0;
    }

    /**
     * 查询符合的行数
     *
     * @param entity 实体参数
     * @return 行数
     */
    default long count(T entity) {
        return count(new SimpleCriteriaQuery<>(entity));
    }

    /**
     * 查询单个实体
     * 如果查询到多条数据会抛出异常!
     *
     * @param entity 实体参数
     * @return 唯一实体结果
     */
    default T selectOne(T entity) {
        List<T> list = selectList(entity);
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() > 1) {
            throw new TooManyResultsException("Expected one result (or null) to be returned by selectOne(), but found: "
                    + list.size());
        } else {
            return null;
        }
    }

    /**
     * 查询所有符合的记录
     *
     * @param entity 实体参数
     * @return 所有符合的记录
     */
    default List<T> selectList(T entity) {
        return selectList(new SimpleCriteriaQuery<>(entity));
    }

    /**
     * 查询所有记录
     *
     * @return 所有记录
     */
    default List<T> selectAll() {
        return selectList(new SimpleCriteriaQuery<>(null));
    }

    /**
     * 查询第一条记录
     *
     * @param entity 实体参数
     * @return 一条记录
     */
    default T selectFist(T entity) {
        List<T> list = selectListByQuantity(entity, 1);
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }

    /**
     * 查询指定数量条记录
     *
     * @param entity   实体参数
     * @param quantity 指定数量
     * @return 指定数量条记录
     */
    default List<T> selectListByQuantity(T entity, int quantity) {
        Page<T> page = new PageImpl<>(quantity);
        page = selectPage(entity, Integer.MAX_VALUE, page);
        return page.getContent();
    }

    /**
     * 分页查询
     *
     * @param entity 实体参数
     * @param page   行选择参数
     * @return 所有符合的记录
     */
    default List<T> selectListLimit(T entity, Page<T> page) {
        return selectListLimit(new SimpleCriteriaQuery<>(entity), page);
    }

    /**
     * 分页查询
     *
     * @param entity 实体参数
     * @param page   分页参数
     * @return 分页数据
     */
    default Page<T> selectPage(T entity, Page<T> page) {
        return selectPage(entity, page, this::count, this::selectListLimit);
    }

    /**
     * 分页查询
     *
     * @param entity 实体参数
     * @param count  总记录数
     * @param page   分页参数
     * @return 分页数据
     */
    default Page<T> selectPage(T entity, long count, Page<T> page) {
        return selectPage(new SimpleCriteriaQuery<>(entity), count, page);
    }

    /**
     * 分页查询
     *
     * @param criteria 查询参数
     * @param page     分页参数
     * @return 分页数据
     */
    default Page<T> selectPage(EntityCriteriaQuery<T> criteria, Page<T> page) {
        return selectPage(criteria, page, this::count, this::selectListLimit);
    }

    /**
     * 分页查询
     *
     * @param criteria 查询参数
     * @param count    总记录数
     * @param page     分页参数
     * @return 分页数据
     */
    default Page<T> selectPage(EntityCriteriaQuery<T> criteria, long count, Page<T> page) {
        Function<EntityCriteriaQuery<T>, Long> countFunction = e -> count;
        return selectPage(criteria, page, countFunction, this::selectListLimit);
    }


    /**
     * 分页查询
     *
     * @param entity         实体参数
     * @param page           分页参数
     * @param countFunction  查询总行数方法
     * @param selectFunction 查询方法
     * @return 分页数据
     */
    default Page<T> selectPage(T entity, Page<T> page, Function<T, Long> countFunction, BiFunction<T, Page<T>, List<T>> selectFunction) {
        return selectPageX(entity, page, countFunction, selectFunction);
    }

    /**
     * 分页查询
     *
     * @param criteria       查询参数
     * @param page           分页参数
     * @param countFunction  查询总行数方法
     * @param selectFunction 查询方法
     * @return 分页数据
     */
    default Page<T> selectPage(EntityCriteriaQuery<T> criteria, Page<T> page, Function<EntityCriteriaQuery<T>, Long> countFunction
            , BiFunction<EntityCriteriaQuery<T>, Page<T>, List<T>> selectFunction) {
        return selectPageX(criteria, page, countFunction, selectFunction);
    }

    /**
     * 分页查询
     *
     * @param x              未确定类型参数
     * @param page           分页参数
     * @param countFunction  查询总行数方法
     * @param selectFunction 查询方法
     * @param <X>            查询参数类型
     * @return 分页数据
     */
    default <X> Page<T> selectPageX(X x, Page<T> page, Function<X, Long> countFunction, BiFunction<X, Page<T>, List<T>> selectFunction) {
        long count = page.getTotal() > 0 ? page.getTotal() : countFunction.apply(x);
        if (count > 0 && (long) (page.getPageNumber() - 1) * page.getPageSize() < count) {
            List<T> list = selectFunction.apply(x, page);
            page.setContent(list);
            page.setTotal(count);
        }
        return page;
    }

}
