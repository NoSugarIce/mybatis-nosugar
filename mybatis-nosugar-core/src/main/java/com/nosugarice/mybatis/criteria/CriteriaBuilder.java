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
import com.nosugarice.mybatis.criteria.select.QueryStructure;
import com.nosugarice.mybatis.criteria.tocolumn.ColumnDelete;
import com.nosugarice.mybatis.criteria.tocolumn.ColumnQuery;
import com.nosugarice.mybatis.criteria.tocolumn.ColumnToColumn;
import com.nosugarice.mybatis.criteria.tocolumn.ColumnUpdate;
import com.nosugarice.mybatis.criteria.tocolumn.LambdaDelete;
import com.nosugarice.mybatis.criteria.tocolumn.LambdaQuery;
import com.nosugarice.mybatis.criteria.tocolumn.LambdaToColumn;
import com.nosugarice.mybatis.criteria.tocolumn.LambdaUpdate;
import com.nosugarice.mybatis.criteria.tocolumn.PropertyDelete;
import com.nosugarice.mybatis.criteria.tocolumn.PropertyQuery;
import com.nosugarice.mybatis.criteria.tocolumn.PropertyToColumn;
import com.nosugarice.mybatis.criteria.tocolumn.PropertyUpdate;
import com.nosugarice.mybatis.criteria.update.CriteriaUpdateImpl;
import com.nosugarice.mybatis.criteria.update.UpdateStructure;
import com.nosugarice.mybatis.criteria.where.Criterion;
import com.nosugarice.mybatis.criteria.where.Where;
import com.nosugarice.mybatis.criteria.where.WhereStructure;
import com.nosugarice.mybatis.criteria.where.criterion.EqualTo;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.registry.EntityMetadataRegistry;
import com.nosugarice.mybatis.util.Preconditions;
import com.nosugarice.mybatis.util.TypeToken;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/12/12
 */
public class CriteriaBuilder {

    //---------------------------lambda---------------------------------------

    public static <T> LambdaQuery<T> lambdaQuery(Class<T> entityClass) {
        return lambdaCriteria(entityClass, new TypeToken<LambdaQuery<T>>() {});
    }

    public static <T> LambdaQuery<T> lambdaQuery(T entity) {
        return lambdaCriteria(entity, new TypeToken<LambdaQuery<T>>() {});
    }

    public static <T> LambdaUpdate<T> lambdaUpdate(Class<T> entityClass) {
        return lambdaCriteria(entityClass, new TypeToken<LambdaUpdate<T>>() {});
    }

    public static <T> LambdaUpdate<T> lambdaUpdate(T entity) {
        return lambdaCriteria(entity, new TypeToken<LambdaUpdate<T>>() {});
    }

    public static <T> LambdaDelete<T> lambdaDelete(Class<T> entityClass) {
        return lambdaCriteria(entityClass, new TypeToken<LambdaDelete<T>>() {});
    }

    public static <T> LambdaDelete<T> lambdaDelete(T entity) {
        return lambdaCriteria(entity, new TypeToken<LambdaDelete<T>>() {});
    }

    //---------------------------property---------------------------------------

    public static <T> PropertyQuery<T> propertyQuery(Class<T> entityClass) {
        return propertyCriteria(entityClass, new TypeToken<PropertyQuery<T>>() {});
    }

    public static <T> PropertyQuery<T> propertyQuery(T entity) {
        return propertyCriteria(entity, new TypeToken<PropertyQuery<T>>() {});
    }

    public static <T> PropertyUpdate<T> propertyUpdate(Class<T> entityClass) {
        return propertyCriteria(entityClass, new TypeToken<PropertyUpdate<T>>() {});
    }

    public static <T> PropertyUpdate<T> propertyUpdate(T entity) {
        return propertyCriteria(entity, new TypeToken<PropertyUpdate<T>>() {});
    }

    public static <T> PropertyDelete<T> propertyDelete(Class<T> entityClass) {
        return propertyCriteria(entityClass, new TypeToken<PropertyDelete<T>>() {});
    }

    public static <T> PropertyDelete<T> propertyDelete(T entity) {
        return propertyCriteria(entity, new TypeToken<PropertyDelete<T>>() {});
    }

    //---------------------------column---------------------------------------

    public static <T> ColumnQuery<T> columnQuery(Class<T> entityClass) {
        return columnCriteria(entityClass, new TypeToken<ColumnQuery<T>>() {});
    }

    public static <T> ColumnQuery<T> columnQuery(T entity) {
        return columnCriteria(entity, new TypeToken<ColumnQuery<T>>() {});
    }

    public static <T> ColumnUpdate<T> columnUpdate(Class<T> entityClass) {
        return columnCriteria(entityClass, new TypeToken<ColumnUpdate<T>>() {});
    }

    public static <T> ColumnUpdate<T> columnUpdate(T entity) {
        return columnCriteria(entity, new TypeToken<ColumnUpdate<T>>() {});
    }

    public static <T> ColumnDelete<T> columnDelete(Class<T> entityClass) {
        return columnCriteria(entityClass, new TypeToken<ColumnDelete<T>>() {});
    }

    public static <T> ColumnDelete<T> columnDelete(T entity) {
        return columnCriteria(entity, new TypeToken<ColumnDelete<T>>() {});
    }

    //---------------------------------?????????????????????----------------------------------------

    /**
     * ??????????????????get??????lambda??????????????????
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
     * ??????????????????get??????lambda??????????????????
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
     * ??????????????????????????????
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
     * ??????????????????????????????
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
     * ?????????????????????????????????
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
     * ?????????????????????????????????
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
            Preconditions.checkNotNull(entityClass, "????????????????????????.");
            X criteria;
            boolean isExtendsCriteria = false;
            Class<?>[] interfaces = null;
            Class<? super X> criteriaRawType = criteriaType.getRawType();
            if (CriteriaQuery.class.isAssignableFrom(criteriaRawType)) {
                criteria = (X) new CriteriaQueryImpl<>(entityClass, toColumn);
                if (CriteriaQuery.class != criteriaRawType) {
                    isExtendsCriteria = true;
                    interfaces = new Class[]{criteriaRawType, QueryStructure.class};
                }
            } else if (CriteriaUpdate.class.isAssignableFrom(criteriaRawType)) {
                criteria = (X) new CriteriaUpdateImpl<>(entityClass, toColumn);
                if (CriteriaUpdate.class != criteriaRawType) {
                    isExtendsCriteria = true;
                    interfaces = new Class[]{criteriaRawType, UpdateStructure.class};
                }
            } else if (CriteriaDelete.class.isAssignableFrom(criteriaRawType)) {
                criteria = (X) new CriteriaDeleteImpl<>(entityClass, toColumn);
                if (CriteriaDelete.class != criteriaRawType) {
                    isExtendsCriteria = true;
                    interfaces = new Class[]{criteriaRawType, WhereStructure.class};
                }
            } else {
                throw new IllegalArgumentException("??????????????????:" + criteriaRawType);
            }

            if (entity != null) {
                List<Criterion> criterionList = entityToCriterion(entity);
                ((Where) criteria).addCriterion(criterionList.toArray(new Criterion[0]));
            }

            if (isExtendsCriteria) {
                Criteria finalCriteria = criteria;
                criteria = (X) Proxy.newProxyInstance(entityClass.getClassLoader(), interfaces
                        , (proxy, method, args) -> method.invoke(finalCriteria, args));
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
