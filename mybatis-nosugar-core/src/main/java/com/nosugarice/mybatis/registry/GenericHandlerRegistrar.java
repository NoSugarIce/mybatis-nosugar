package com.nosugarice.mybatis.registry;

import com.nosugarice.mybatis.handler.AbstractGenericHandler;
import com.nosugarice.mybatis.util.ReflectionUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dingjingyang@foxmail.com
 * @date 2024/5/12
 */
public class GenericHandlerRegistrar {

    private final Map<Class<? extends AbstractGenericHandler>, Map<Type, AbstractGenericHandler>> handlerMap = new ConcurrentHashMap<>();

    public boolean hasTypeHandler(Class<? extends AbstractGenericHandler> handlerClass, Type type) {
        return handlerMap.getOrDefault(handlerClass, new HashMap<>()).containsKey(type);
    }

    public void register(Class<? extends AbstractGenericHandler> handlerClass, Type type) {
        AbstractGenericHandler handler = ReflectionUtils.newInstance(handlerClass, type);
        handlerMap.computeIfAbsent(handlerClass, k -> new ConcurrentHashMap<>()).put(type, handler);
    }

    public synchronized AbstractGenericHandler getHandler(Class<? extends AbstractGenericHandler> handlerClass, Type type) {
        if (!hasTypeHandler(handlerClass, type)) {
            register(handlerClass, type);
        }
        return handlerMap.getOrDefault(handlerClass, new HashMap<>()).get(type);
    }

}
