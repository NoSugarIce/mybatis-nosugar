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

package com.nosugarice.mybatis.query.criteria.tocolumn;

import com.nosugarice.mybatis.exception.NoSugarException;
import com.nosugarice.mybatis.mapper.function.FunS;
import com.nosugarice.mybatis.query.criteria.ConvertToColumn;
import com.nosugarice.mybatis.registry.EntityMetadataRegistry;
import com.nosugarice.mybatis.util.LambdaUtils;

import java.util.Optional;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public interface LambdaToColumn<T> extends ConvertToColumn<FunS.Getter<T, ?>> {

    @Override
    default String toColumn(FunS.Getter<T, ?> column, Class<?> entityClass) {
        return Optional.of(column)
                .map(LambdaToColumn::getPropertyName)
                .map(property -> EntityMetadataRegistry.Holder.getInstance().getColumnByProperty(entityClass, property))
                .orElseThrow(() -> new NullPointerException("未找到属性对应的列信息!"));
    }

    static String getPropertyName(FunS.Getter<?, ?> lambda) {
        String methodName = LambdaUtils.getFunctionalName(lambda);
        final String get = "get";
        final String is = "is";

        String name;
        if (methodName.startsWith(get)) {
            name = methodName.substring(get.length());
        } else if (methodName.startsWith(is)) {
            name = methodName.substring(is.length());
        } else {
            throw new NoSugarException("[? extends Getter] 不是一个有效的属性方法!");
        }
        return name.length() > 1
                ? Character.toLowerCase(name.charAt(0)) + name.substring(1)
                : String.valueOf(Character.toLowerCase(name.charAt(0)));
    }

}
