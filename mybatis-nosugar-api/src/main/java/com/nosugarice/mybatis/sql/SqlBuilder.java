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

package com.nosugarice.mybatis.sql;

import com.nosugarice.mybatis.criteria.CriteriaDelete;
import com.nosugarice.mybatis.criteria.CriteriaQuery;
import com.nosugarice.mybatis.criteria.CriteriaUpdate;
import com.nosugarice.mybatis.mapper.function.FunS;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.Set;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/2
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlBuilder {

    enum SqlFunction {
        SELECT_BY_ID() {
            @Override
            public <T, ID> FunS.Param2<ProviderTempLate, ID, SqlAndParameterBind> providerFun() {
                return ProviderTempLate::selectById;
            }
        },
        SELECT_BY_IDS() {
            @Override
            public <T, ID> FunS.Param2<ProviderTempLate, Collection<ID>, SqlAndParameterBind> providerFun() {
                return ProviderTempLate::selectByIds;
            }
        },
        SELECT_LIST() {
            @Override
            public <T, ID> FunS.Param2<ProviderTempLate, CriteriaQuery<T, ?, ?>, SqlAndParameterBind> providerFun() {
                return ProviderTempLate::selectList;
            }
        },
        INSERT() {
            @Override
            public <T, ID> FunS.Param2<ProviderTempLate, T, SqlAndParameterBind> providerFun() {
                return ProviderTempLate::insert;
            }
        },
        INSERT_SELECTIVE() {
            @Override
            public <T, ID> FunS.Param2<ProviderTempLate, T, SqlAndParameterBind> providerFun() {
                return ProviderTempLate::insertSelective;
            }
        },
        UPDATE_BY_ID() {
            @Override
            public <T, ID> FunS.Param3<ProviderTempLate, T, Boolean, SqlAndParameterBind> providerFun() {
                return ProviderTempLate::updateById;
            }
        },
        UPDATE_BY_ID_CHOSE_PROPERTY() {
            @Override
            public <T, ID> FunS.Param3<ProviderTempLate, T, Set<String>, SqlAndParameterBind> providerFun() {
                return ProviderTempLate::updateByIdChoseProperty;
            }
        },
        UPDATE() {
            @Override
            public <T, ID> FunS.Param2<ProviderTempLate, CriteriaUpdate<T, ?, ?>, SqlAndParameterBind> providerFun() {
                return ProviderTempLate::update;
            }
        },
        DELETE_BY_ID() {
            @Override
            public <T, ID> FunS.Param2<ProviderTempLate, ID, SqlAndParameterBind> providerFun() {
                return ProviderTempLate::deleteById;
            }
        },
        DELETE_BY_IDS() {
            @Override
            public <T, ID> FunS.Param2<ProviderTempLate, Collection<ID>, SqlAndParameterBind> providerFun() {
                return ProviderTempLate::deleteByIds;
            }
        },
        DELETE() {
            @Override
            public <T, ID> FunS.Param2<ProviderTempLate, CriteriaDelete<T, ?, ?>, SqlAndParameterBind> providerFun() {
                return ProviderTempLate::delete;
            }
        },
        LOGIC_DELETE_BY_ID() {
            @Override
            public <T, ID> FunS.Param2<ProviderTempLate, ID, SqlAndParameterBind> providerFun() {
                return ProviderTempLate::logicDeleteById;
            }
        },
        LOGIC_DELETE_BY_IDS() {
            @Override
            public <T, ID> FunS.Param2<ProviderTempLate, Collection<ID>, SqlAndParameterBind> providerFun() {
                return ProviderTempLate::logicDeleteByIds;
            }
        },
        LOGIC_DELETE() {
            @Override
            public <T, ID> FunS.Param2<ProviderTempLate, CriteriaDelete<T, ?, ?>, SqlAndParameterBind> providerFun() {
                return ProviderTempLate::logicDelete;
            }
        },
        ;

        public abstract <T, ID> FunS<SqlAndParameterBind> providerFun();

    }

    SqlFunction sqlFunction();

    /** 固定参数,仅适应单个参数 */
    boolean fixedParameter() default false;

}
