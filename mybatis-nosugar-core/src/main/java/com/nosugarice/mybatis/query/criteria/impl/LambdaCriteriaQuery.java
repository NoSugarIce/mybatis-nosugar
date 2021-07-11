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

package com.nosugarice.mybatis.query.criteria.impl;

import com.nosugarice.mybatis.builder.sql.MetadataCache;
import com.nosugarice.mybatis.exception.NoSugarException;
import com.nosugarice.mybatis.mapper.function.FunS;
import com.nosugarice.mybatis.util.LambdaUtils;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class LambdaCriteriaQuery<T> extends AbstractCriteriaQuery<T, FunS.Getter<T, ?>> {

    public LambdaCriteriaQuery(T entity) {
        super(entity);
    }

    @Override
    public Function<FunS.Getter<T, ?>, String> convert() {
        return getter -> Optional.of(getter)
                .map(LambdaCriteriaQuery::getPropertyName)
                .map(property -> MetadataCache.getColumnByProperty(getEntityClass(), property))
                .orElseThrow(() -> new NullPointerException("未找到属性对应的列信息!"));
    }

    private static String getPropertyName(FunS.Getter<?, ?> lambda) {
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
