package com.nosugarice.mybatis.criteria;

import com.nosugarice.mybatis.utils.ServiceLoaderUtils;

/**
 * 实体转到结构体的相关操作
 *
 * @author dingjingyang@foxmail.com
 * @date 2021/12/19
 */
public interface EntityToCriterion {

    /**
     * 实体对象转简单的查询体
     *
     * @param entity
     * @param <T>
     * @return
     */
    <T> CriteriaQuery<T, String, ?> entityToSimpleCriteriaQuery(T entity);

    /**
     * 实体对象转更新查询体
     *
     * @param entity
     * @param <T>
     * @return
     */
    <T> CriteriaUpdate<T, String, ?> entityToCriteriaUpdate(T entity);

    /**
     * 实体对象转删除查询体
     *
     * @param entity
     * @param <T>
     * @return
     */
    <T> CriteriaDelete<T, String, ?> entityToCriteriaDelete(T entity);

    /**
     * 将实体属性合并到更新结构体
     *
     * @param entity
     * @param criteriaUpdate
     * @param nullable
     * @param <T>
     * @return
     */
    <T, C> CriteriaUpdate<T, C, ?> mergeCriteriaUpdate(T entity, CriteriaUpdate<T, C, ?> criteriaUpdate, boolean nullable);

    /**
     * 获取实例
     *
     * @return
     */
    static EntityToCriterion getInstance() {
        return Holder.INSTANCE;
    }

    class Holder {
        private static final EntityToCriterion INSTANCE = ServiceLoaderUtils.loadSingleInstance(EntityToCriterion.class);
    }

}
