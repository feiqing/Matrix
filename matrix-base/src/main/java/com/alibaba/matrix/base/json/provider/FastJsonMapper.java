package com.alibaba.matrix.base.json.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.matrix.base.json.JsonMapper;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2018/8/30 12:45.
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
