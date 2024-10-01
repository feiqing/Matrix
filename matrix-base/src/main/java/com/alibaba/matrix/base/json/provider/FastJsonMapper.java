package com.alibaba.matrix.base.json.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.matrix.base.json.JsonMapper;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/1 15:46.
 */
public class FastJsonMapper implements JsonMapper {

    @Override
    public String toJson(Object obj) {
        return JSON.toJSONString(obj);
    }

    @Override
    public <T> T fromJson(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    @Override
    public <T> T fromJson(String json, Type type) {
        return JSON.parseObject(json, type);
    }

    @Override
    public <T> T fromJson(String json, TypeToken<T> typeToken) {
        return JSON.parseObject(json, typeToken.getType());
    }
}
