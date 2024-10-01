package com.alibaba.matrix.base.json.provider;

import com.alibaba.matrix.base.json.JsonMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/1 15:39.
 */
public class GsonJsonMapper implements JsonMapper {

    private static final Gson gson = new Gson();

    @Override
    public String toJson(Object obj) {
        return gson.toJson(obj);
    }

    @Override
    public <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    @Override
    public <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }

    @Override
    public <T> T fromJson(String json, TypeToken<T> typeToken) {
        return gson.fromJson(json, typeToken.getType());
    }
}
