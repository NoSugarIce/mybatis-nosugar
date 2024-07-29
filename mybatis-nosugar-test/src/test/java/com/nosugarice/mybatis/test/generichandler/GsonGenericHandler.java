package com.nosugarice.mybatis.test.generichandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nosugarice.mybatis.handler.AbstractGenericHandler;

import java.lang.reflect.Type;

/**
 * @author dingjingyang@foxmail.com
 * @date 2024/5/12
 */
public class GsonGenericHandler extends AbstractGenericHandler {

    private static final Gson GSON = new GsonBuilder().create();

    public GsonGenericHandler(Type type) {
        super(type);
    }

    @Override
    public String toStr(Object parameter) {
        return GSON.toJson(parameter);
    }

    @Override
    public Object strToObject(String str, Type type) {
        return GSON.fromJson(str, type);
    }

}
