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
import com.nosugarice.mybatis.criteria.CriteriaQuery;
import com.nosugarice.mybatis.criteria.EntityToCriterion;
import com.nosugarice.mybatis.domain.Page;
import com.nosugarice.mybatis.mapper.MapperParam;
import com.nosugarice.mybatis.sql.SqlBuilder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.exceptions.TooManyResultsException;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author dingjingyang@foxmail.com
 * @date 2017/8/29
 */
public interface SelectCriteriaMapper<T> extends SelectMapper {

    /**
     * 查询符合条件的记录
     *
     * @param criteria 查询条件封装
     * @return
     */
    @SqlBuilder(sqlFunction = SqlBuilder.SqlFunction.SELECT_LIST)
    <C> List<T> selectList(@Param(MapperParam.CRITERIA) CriteriaQuery<T, C> criteria);

    /**
     * 查询符合条件的记录数
     *
     * @param criteria 查询条件封装
     * @return
     */
    @Provider(Provider.Type.COUNT)
    <C> long count(@Param(MapperParam.CRITERIA) CriteriaQuery<T, C> criteria);

    /**
     * 判断是否存在
     *
     * @param criteria 查询条件封装
     * @return 是否存在包装
     */
    @Provider(Provider.Type.EXISTS)
    <C> Optional<Integer> exists(@Param(MapperParam.CRITERIA) CriteriaQuery<T, C> criteria);

    /**
     * 查询所有记录
     *
     * @return 所有记录
     */
    default <C> List<T> selectAll() {
        return selectList((CriteriaQuery<T, C>) null);
    }


    /**
     * 查询符合的行数
     *
     * @param entity 实体参数
     * @return 行数
     */
    default long count(T entity) {
        return count(EntityToCriterion.getInstance().entityToSimpleCriteriaQuery(entity));
    }

    /**
     * 查询符合的行数
     *
     * @param entity 实体参数
     * @return 行数
     */
    default boolean exists(T entity) {
        return exists(EntityToCriterion.getInstance().entityToSimpleCriteriaQuery(entity)).isPresent();
    }

    /**
     * 查询单个实体
     * 如果查询到多条数据会抛出异常!
     *
     * @param entity 实体参数
     * @return 唯一实体结果
     */
    default T selectOne(T entity) {
        List<T> list = selectListLimit(entity, 2);
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() > 1) {
            throw new TooManyResultsException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
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
        return selectList(EntityToCriterion.getInstance().entityToSimpleCriteriaQuery(entity));
    }

    /**
     * 查询指定数量条记录
     *
     * @param entity 实体参数
     * @param limit  指定数量
     * @return 指定数量条记录
     */
    default List<T> selectListLimit(T entity, int limit) {
        return selectList(EntityToCriterion.getInstance().entityToSimpleCriteriaQuery(entity).limit(limit));
    }

    /**
     * 分页查询
     *
     * @param entity 实体参数
     * @param page   行选择参数
     * @return 所有符合的记录
     */
    default List<T> selectListLimit(T entity, Page<T> page) {
        return selectList(EntityToCriterion.getInstance().entityToSimpleCriteriaQuery(entity).limit(page));
    }

    /**
     * 分页查询
     *
     * @param criteria 查询条件封装
     * @param page     分页参数
     * @return 所有符合的记录
     */
    default <C> List<T> selectListLimit(CriteriaQuery<T, C> criteria, Page<T> page) {
        return selectList(criteria.limit(page));
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
     * @param criteria 查询参数
     * @param page     分页参数
     * @return 分页数据
     */
    default <C> Page<T> selectPage(CriteriaQuery<T, C> criteria, Page<T> page) {
        return selectPage(criteria, page, this::count, this::selectListLimit);
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
    default <C> Page<T> selectPage(CriteriaQuery<T, C> criteria, Page<T> page, Function<CriteriaQuery<T, C>, Long> countFunction
            , BiFunction<CriteriaQuery<T, C>, Page<T>, List<T>> selectFunction) {
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
        if (count > 0 && (long) (page.getNumber() - 1) * page.getSize() < count) {
            List<T> list = selectFunction.apply(x, page);
            page.setContent(list);
            page.setTotal(count);
        }
        return page;
    }

}