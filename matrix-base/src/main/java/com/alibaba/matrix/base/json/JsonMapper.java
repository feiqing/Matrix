package com.alibaba.matrix.base.json;

import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/8/30 17:45.
 */
public interface JsonMapper {

    String toJson(Object obj);

    <T> T fromJson(String json, Class<T> clazz);

    <T> T fromJson(String json, Type type);

    <T> T fromJson(String json, TypeToken<T> typeToken);
}
