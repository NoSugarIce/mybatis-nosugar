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

package com.nosugarice.mybatis.mapper.function;

import java.io.Serializable;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/6/27
 */
@SuppressWarnings("unchecked")
public interface FunS<R> extends Serializable {

    default R invoke(Object... args) {
        return null;
    }

    @FunctionalInterface
    interface Param0<R> extends FunS<R> {

        R apply();

        @Override
        default R invoke(Object... args) {
            return apply();
        }
    }

    @FunctionalInterface
    interface Param1<T1, R> extends FunS<R> {

        R apply(T1 t1);

        @Override
        default R invoke(Object... args) {
            return apply((T1) args[0]);
        }
    }

    @FunctionalInterface
    interface Param2<T1, T2, R> extends FunS<R> {

        R apply(T1 t1, T2 t2);

        @Override
        default R invoke(Object... args) {
            return apply((T1) args[0], (T2) args[1]);
        }
    }

    @FunctionalInterface
    interface Param3<T1, T2, T3, R> extends FunS<R> {

        R apply(T1 t1, T2 t2, T3 t3);

        @Override
        default R invoke(Object... args) {
            return apply((T1) args[0], (T2) args[1], (T3) args[2]);
        }
    }

    @FunctionalInterface
    interface Param4<T1, T2, T3, T4, R> extends FunS<R> {

        R apply(T1 t1, T2 t2, T3 t3, T4 t4);

        @Override
        default R invoke(Object... args) {
            return apply((T1) args[0], (T2) args[1], (T3) args[2], (T4) args[3]);
        }
    }

    @FunctionalInterface
    interface Param5<T1, T2, T3, T4, T5, R> extends FunS<R> {

        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

        @Override
        default R invoke(Object... args) {
            return apply((T1) args[0], (T2) args[1], (T3) args[2], (T4) args[3], (T5) args[4]);
        }
    }

}
