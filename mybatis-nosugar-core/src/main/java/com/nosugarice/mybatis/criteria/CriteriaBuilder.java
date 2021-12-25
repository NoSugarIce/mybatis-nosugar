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

package com.nosugarice.mybatis.criteria;

import com.nosugarice.mybatis.config.EntityMetadata;
import com.nosugarice.mybatis.criteria.delete.CriteriaDeleteImpl;
import com.nosugarice.mybatis.criteria.select.CriteriaQueryImpl;
import com.nosugarice.mybatis.criteria.tocolumn.ColumnToColumn;
import com.nosugarice.mybatis.criteria.tocolumn.LambdaToColumn;
import com.nosugarice.mybatis.criteria.tocolumn.PropertyToColumn;
import com.nosugarice.mybatis.criteria.update.CriteriaUpdateImpl;
import com.nosugarice.mybatis.criteria.where.Criterion;
import com.nosugarice.mybatis.criteria.where.Where;
import com.nosugarice.mybatis.criteria.where.criterion.EqualTo;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.registry.EntityMetadataRegistry;
import com.nosugarice.mybatis.util.Preconditions;
import com.nosugarice.mybatis.util.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/12/12
 */
public class CriteriaBuilder {

    //---------------------------lambda---------------------------------------

    public static <T> CriteriaQuery<T, Getter<T, ?>> lambdaQuery(Class<T> entityClass) {
        return lambdaCriteria(entityClass, new TypeToken<CriteriaQuery<T, Getter<T, ?>>>() {
        });
    }

    public static <T> CriteriaQuery<T, Getter<T, ?>> lambdaQuery(T entity) {
        return lambdaCriteria(entity, new TypeToken<CriteriaQuery<T, Getter<T, ?>>>() {
        });
    }

    public static <T> CriteriaUpdate<T, Getter<T, ?>> lambdaUpdate(Class<T> entityClass) {
        return lambdaCriteria(entityClass, new TypeToken<CriteriaUpdate<T, Getter<T, ?>>>() {
        });
    }

    public static <T> CriteriaUpdate<T, Getter<T, ?>> lambdaUpdate(T entity) {
        return lambdaCriteria(entity, new TypeToken<CriteriaUpdate<T, Getter<T, ?>>>() {
        });
    }

    public static <T> CriteriaDelete<T, Getter<T, ?>> lambdaDelete(Class<T> entityClass) {
        return lambdaCriteria(entityClass, new TypeToken<CriteriaDelete<T, Getter<T, ?>>>() {
        });
    }

    public static <T> CriteriaDelete<T, Getter<T, ?>> lambdaDelete(T entity) {
        return lambdaCriteria(entity, new TypeToken<CriteriaDelete<T, Getter<T, ?>>>() {
        });
    }

    //---------------------------property---------------------------------------

    public static <T> CriteriaQuery<T, String> propertyQuery(Class<T> entityClass) {
        return propertyCriteria(entityClass, new TypeToken<CriteriaQuery<T, String>>() {
        });
    }

    public static <T> CriteriaQuery<T, String> propertyQuery(T entity) {
        return propertyCriteria(entity, new TypeToken<CriteriaQuery<T, String>>() {
        });
    }

    public static <T> CriteriaUpdate<T, String> propertyUpdate(Class<T> entityClass) {
        return propertyCriteria(entityClass, new TypeToken<CriteriaUpdate<T, String>>() {
        });
    }

    public static <T> CriteriaUpdate<T, String> propertyUpdate(T entity) {
        return propertyCriteria(entity, new TypeToken<CriteriaUpdate<T, String>>() {
        });
    }

    public static <T> CriteriaDelete<T, String> propertyDelete(Class<T> entityClass) {
        return propertyCriteria(entityClass, new TypeToken<CriteriaDelete<T, String>>() {
        });
    }

    public static <T> CriteriaDelete<T, String> propertyDelete(T entity) {
        return propertyCriteria(entity, new TypeToken<CriteriaDelete<T, String>>() {
        });
    }

    //---------------------------column---------------------------------------

    public static <T> CriteriaQuery<T, String> columnQuery(Class<T> entityClass) {
        return columnCriteria(entityClass, new TypeToken<CriteriaQuery<T, String>>() {
        });
    }

    public static <T> CriteriaQuery<T, String> columnQuery(T entity) {
        return columnCriteria(entity, new TypeToken<CriteriaQuery<T, String>>() {
        });
    }

    public static <T> CriteriaUpdate<T, String> columnUpdate(Class<T> entityClass) {
        return columnCriteria(entityClass, new TypeToken<CriteriaUpdate<T, String>>() {
        });
    }

    public static <T> CriteriaUpdate<T, String> columnUpdate(T entity) {
        return columnCriteria(entity, new TypeToken<CriteriaUpdate<T, String>>() {
        });
    }

    public static <T> CriteriaDelete<T, String> columnDelete(Class<T> entityClass) {
        return columnCriteria(entityClass, new TypeToken<CriteriaDelete<T, String>>() {
        });
    }

    public static <T> CriteriaDelete<T, String> columnDelete(T entity) {
        return columnCriteria(entity, new TypeToken<CriteriaDelete<T, String>>() {
        });
    }

    //---------------------------------未确定类型构建----------------------------------------

    /**
     * 创建基于实体get方法lambda形式的结构体
     *
     * @param entity
     * @param typeToken
     * @param <T>
     * @param <X>
     * @return
     */
    private static <T, X extends Criteria> X lambdaCriteria(T entity, TypeToken<X> typeToken) {
        return new ColumnBuilder<T, Getter<T, ?>>()
                .withType(entity)
                .withConvertToColumn(LambdaToColumn.getInstance())
                .build(typeToken);
    }

    /**
     * 创建基于实体get方法lambda形式的结构体
     *
     * @param entityClass
     * @param typeToken
     * @param <T>
     * @param <X>
     * @return
     */
    private static <T, X extends Criteria> X lambdaCriteria(Class<T> entityClass, TypeToken<X> typeToken) {
        return new ColumnBuilder<T, Getter<T, ?>>()
                .withType(entityClass)
                .withConvertToColumn(LambdaToColumn.getInstance())
                .build(typeToken);
    }

    /**
     * 创建基于属性的结构体
     *
     * @param entity
     * @param typeToken
     * @param <T>
     * @param <X>
     * @return
     */
    private static <T, X extends Criteria> X propertyCriteria(T entity, TypeToken<X> typeToken) {
        return columnOrPropertyCriteria(entity, null, typeToken, true);
    }

    /**
     * 创建基于属性的结构体
     *
     * @param entityClass
     * @param typeToken
     * @param <T>
     * @param <X>
     * @return
     */
    private static <T, X extends Criteria> X propertyCriteria(Class<T> entityClass, TypeToken<X> typeToken) {
        return columnOrPropertyCriteria(null, entityClass, typeToken, true);
    }

    /**
     * 创建基于表字段的结构体
     *
     * @param entity
     * @param typeToken
     * @param <T>
     * @param <X>
     * @return
     */
    private static <T, X extends Criteria> X columnCriteria(T entity, TypeToken<X> typeToken) {
        return columnOrPropertyCriteria(entity, null, typeToken, false);
    }

    /**
     * 创建基于表字段的结构体
     *
     * @param entityClass
     * @param typeToken
     * @param <T>
     * @param <X>
     * @return
     */
    private static <T, X extends Criteria> X columnCriteria(Class<T> entityClass, TypeToken<X> typeToken) {
        return columnOrPropertyCriteria(null, entityClass, typeToken, false);
    }

    private static <T, X extends Criteria> X columnOrPropertyCriteria(T entity, Class<T> entityClass
            , TypeToken<X> typeToken, boolean isProperty) {
        return new ColumnBuilder<T, String>()
                .withType(entity)
                .withType(entityClass)
                .withConvertToColumn(isProperty ? PropertyToColumn.getInstance() : ColumnToColumn.getInstance())
                .build(typeToken);
    }

    //-------------------------------------------------------------------------

    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    private static class ColumnBuilder<T, C> {

        private T entity;
        private Class<T> entityClass;
        private ToColumn<C> toColumn;

        private ColumnBuilder<T, C> withType(T entity) {
            if (entity != null) {
                this.entity = entity;
                this.entityClass = (Class<T>) entity.getClass();
            }
            return this;
        }

        private ColumnBuilder<T, C> withType(Class<T> entityClass) {
            if (entityClass != null) {
                this.entityClass = entityClass;
            }
            return this;
        }

        private ColumnBuilder<T, C> withConvertToColumn(ToColumn<?> toColumn) {
            this.toColumn = (ToColumn<C>) toColumn;
            return this;
        }

        private <X extends Criteria> X build(TypeToken<X> criteriaType) {
            Preconditions.checkNotNull(entityClass, "实体类型缺少参数.");
            X criteria;
            switch (criteriaType.getRawType().getSimpleName()) {
                case "CriteriaQuery":
                    criteria = (X) new CriteriaQueryImpl<>(entityClass, toColumn);
                    break;
                case "CriteriaUpdate":
                    criteria = (X) new CriteriaUpdateImpl<>(entityClass, toColumn);
                    break;
                case "CriteriaDelete":
                    criteria = (X) new CriteriaDeleteImpl<>(entityClass, toColumn);
                    break;
                default:
                    throw new IllegalArgumentException("未匹配CriteriaType类型.");
            }
            if (entity != null) {
                List<Criterion> criterionList = entityToCriterion(entity);
                ((Where) criteria).addCriterion(criterionList.toArray(new Criterion[0]));
            }
            return criteria;
        }

        private List<Criterion> entityToCriterion(T entity) {
            List<Criterion> entityCriterions = new ArrayList<>();
            if (entity != null) {
                EntityMetadata entityMetadata = EntityMetadataRegistry.getInstance().getEntityMetadata(entity.getClass());
                for (RelationalProperty property : entityMetadata.getRelationalEntity().getProperties()) {
                    Object value = property.getValue(entity);
                    if (value != null) {
                        entityCriterions.add(new EqualTo<>(property.getColumn(), value));
                    }
                }
            }
            return entityCriterions;
        }

    }

}
