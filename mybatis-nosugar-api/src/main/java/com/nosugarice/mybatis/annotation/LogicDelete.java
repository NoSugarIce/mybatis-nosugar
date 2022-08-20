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

package com.nosugarice.mybatis.annotation;

import com.nosugarice.mybatis.handler.ValueHandler;

import java.io.Serializable;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 逻辑删除
 * 期待 未来Java @interface 可以更碉堡一点
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/12/6
 */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogicDelete {

    String NULL = "NULL";
    String NOW = "NOW";

    //默认值
    String defaultValue() default NULL;

    //逻辑删除值
    String deleteValue();

    Class<? extends ValueHandler<? extends Serializable>> deleteValueHandler() default ColumnOptions.VoidHandler.class;

}
