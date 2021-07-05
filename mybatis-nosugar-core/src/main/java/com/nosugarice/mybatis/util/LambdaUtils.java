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

package com.nosugarice.mybatis.util;

import com.nosugarice.mybatis.exception.NoSugarException;
import com.nosugarice.mybatis.mapper.function.FunS;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/6/27
 */
public class LambdaUtils {

    public static String getFunctionalName(FunS.Getter<?, ?> getter) {
        return getFunctionalName((Serializable) getter);
    }

    public static String getFunctionalName(Serializable lambdaSerializable) {
        try {
            Method method = lambdaSerializable.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(lambdaSerializable);
            return serializedLambda.getImplMethodName();
        } catch (ReflectiveOperationException ex) {
            throw new NoSugarException(ex);
        }
    }

}
