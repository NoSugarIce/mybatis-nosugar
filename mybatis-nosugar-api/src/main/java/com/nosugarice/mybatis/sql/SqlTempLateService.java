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

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/6
 */
public interface SqlTempLateService {

    String selectById();

    String selectByIds();

    String selectList();

    String insert();

    String insertNullable();

    String insertBatch();

    String updateById();

    String updateByIdChoseKey();

    String updateNullableById();

    String update();

    String updateNullable();

    String deleteById();

    String deleteByIds();

    String delete();

    String logicDeleteById();

    String logicDeleteByIds();

    String logicDelete();

    //--------------jpa------------------------

    String provideFind();

    String provideCount();

    String provideDelete();

}
