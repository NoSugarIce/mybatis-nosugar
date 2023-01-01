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

package com.nosugarice.mybatis.util;

import com.nosugarice.mybatis.exception.NoSugarException;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * @author dingjingyang@foxmail.com
 * @date 2018/4/23
 */
public class Preconditions {

    private static final Log LOG = LogFactory.getLog(Preconditions.class);

    /**
     * 判断条件,不符合且致命级别将报错,其他只是warn 提醒
     *
     * @param expression
     * @param message
     */
    public static void checkArgument(boolean expression, String message) {
        checkArgument(expression, true, message);
    }

    /**
     * 判断条件,不符合且致命级别将报错,其他只是warn 提醒
     *
     * @param expression
     * @param fatal
     * @param message
     */
    public static void checkArgument(boolean expression, boolean fatal, String message) {
        if (!expression) {
            if (fatal) {
                throw new NoSugarException(message);
            } else {
                LOG.warn(message);
            }
        }
    }

    /**
     * 判断对象是否为空,为空将抛出异常
     *
     * @param reference
     * @param message
     * @param <T>
     * @return
     */
    public static <T> T checkNotNull(T reference, String message) {
        if (reference == null) {
            throw new NoSugarException(message);
        }
        return reference;
    }

}
