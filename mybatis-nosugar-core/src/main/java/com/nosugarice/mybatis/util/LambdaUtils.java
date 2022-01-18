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

import com.nosugarice.mybatis.criteria.Getter;
import com.nosugarice.mybatis.exception.NoSugarException;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/6/27
 */
public class LambdaUtils {

    public static <T> String getFunctionalName(Getter<T, ?> getter) {
        return getFunctionalName((Serializable) getter);
    }

    public static String getFunctionalName(Serializable lambdaSerializable) {
        return getLambdaInfo(lambdaSerializable).getMethodName();
    }

    public static LambdaInfo getLambdaInfo(Serializable lambdaSerializable) {
        try {
            Method method = lambdaSerializable.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(lambdaSerializable);
            return new LambdaInfo(serializedLambda);
        } catch (ReflectiveOperationException ex) {
            throw new NoSugarException(ex);
        }
    }

    public static <T> LambdaInfo getLambdaInfo(Getter<T, ?> getter) {
        return getLambdaInfo((Serializable) getter);
    }

    public static class LambdaInfo {

        private final String className;
        private final String methodName;
        private final Object firstCapturedArg;

        public LambdaInfo(SerializedLambda serializedLambda) {
            this.className = serializedLambda.getImplClass().replace("/", ".");
            this.methodName = serializedLambda.getImplMethodName();
            this.firstCapturedArg = serializedLambda.getCapturedArgCount() > 0 ? serializedLambda.getCapturedArg(0) : null;
        }

        public String getClassName() {
            return className;
        }

        public String getMethodName() {
            return methodName;
        }

        public Class<?> getType() {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new NoSugarException(e);
            }
        }

        public Object getFirstCapturedArg() {
            return firstCapturedArg;
        }

    }

}
