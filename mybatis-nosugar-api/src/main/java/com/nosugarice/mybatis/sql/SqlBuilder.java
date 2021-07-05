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

package com.nosugarice.mybatis.sql;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/2
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlBuilder {

    enum SqlSourceFunction implements SqlProvider {
        SELECT_BY_ID() {
            @Override
            public String provide(SqlTempLateService sqlTempLateService) {
                return sqlTempLateService.selectById();
            }
        },
        SELECT_BY_IDS() {
            @Override
            public String provide(SqlTempLateService sqlTempLateService) {
                return sqlTempLateService.selectByIds();
            }
        },
        SELECT_LIST() {
            @Override
            public String provide(SqlTempLateService sqlTempLateService) {
                return sqlTempLateService.selectList();
            }
        },
        INSERT() {
            @Override
            public String provide(SqlTempLateService sqlTempLateService) {
                return sqlTempLateService.insert();
            }
        },
        INSERT_NULLABLE() {
            @Override
            public String provide(SqlTempLateService sqlTempLateService) {
                return sqlTempLateService.insertNullable();
            }
        },
        INSERT_BATCH() {
            @Override
            public String provide(SqlTempLateService sqlTempLateService) {
                return sqlTempLateService.insertBatch();
            }
        },
        UPDATE_BY_ID() {
            @Override
            public String provide(SqlTempLateService sqlTempLateService) {
                return sqlTempLateService.updateById();
            }
        },
        UPDATE_BY_ID_CHOSE_KEY() {
            @Override
            public String provide(SqlTempLateService sqlTempLateService) {
                return sqlTempLateService.updateByIdChoseKey();
            }
        },
        UPDATE_NULLABLE_BY_ID() {
            @Override
            public String provide(SqlTempLateService sqlTempLateService) {
                return sqlTempLateService.updateNullableById();
            }
        },
        UPDATE() {
            @Override
            public String provide(SqlTempLateService sqlTempLateService) {
                return sqlTempLateService.update();
            }
        },
        UPDATE_NULLABLE() {
            @Override
            public String provide(SqlTempLateService sqlTempLateService) {
                return sqlTempLateService.updateNullable();
            }
        },
        DELETE_BY_ID() {
            @Override
            public String provide(SqlTempLateService sqlTempLateService) {
                return sqlTempLateService.deleteById();
            }
        },
        DELETE_BY_IDS() {
            @Override
            public String provide(SqlTempLateService sqlTempLateService) {
                return sqlTempLateService.deleteByIds();
            }
        },
        DELETE() {
            @Override
            public String provide(SqlTempLateService sqlTempLateService) {
                return sqlTempLateService.delete();
            }
        },
        LOGIC_DELETE_BY_ID() {
            @Override
            public String provide(SqlTempLateService sqlTempLateService) {
                return sqlTempLateService.logicDeleteById();
            }
        },
        LOGIC_DELETE_BY_IDS() {
            @Override
            public String provide(SqlTempLateService sqlTempLateService) {
                return sqlTempLateService.logicDeleteByIds();
            }
        },
        LOGIC_DELETE() {
            @Override
            public String provide(SqlTempLateService sqlTempLateService) {
                return sqlTempLateService.logicDelete();
            }
        },
    }

    SqlSourceFunction sqlSourceFunction();

}
