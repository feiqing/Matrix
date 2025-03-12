package com.alibaba.matrix.base.json;

import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * The JsonMapper interface provides methods to convert Java objects to JSON strings and vice versa.
 * It supports conversion to and from generic types using Class, Type, and TypeToken.
 *
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2018/8/30 11:45.
 */
public interface JsonMapper {

    /**
     * Converts a Java object to a JSON string.
     *
     * @param obj the Java object to be converted
     * @return the JSON string representation of the object
     */
    String toJson(Object obj);

    /**
     * Converts a JSON string to a Java object of the specified class type.
     *
     * @param json  the JSON string to be converted
     * @param clazz the class type of the resulting Java object
     * @param <T>   the type of the resulting Java object
     * @return the Java object of the specified class type
     */
    <T> T fromJson(String json, Class<T> clazz);

    /**
     * Converts a JSON string to a Java object of the specified type.
     *
     * @param json the JSON string to be converted
     * @param type the type of the resulting Java object
     * @param <T> the type of the resulting Java object
     * @return the Java object of the specified type
     */
    <T> T fromJson(String json, Type type);

    /**
     * Converts a JSON string to a Java object of the specified type using TypeToken.
     *
     * @param json the JSON string to be converted
     * @param typeToken the TypeToken representing the type of the resulting Java object
     * @param <T> the type of the resulting Java object
     * @return the Java object of the specified type
     */
    <T> T fromJson(String json, TypeToken<T> typeToken);
}
