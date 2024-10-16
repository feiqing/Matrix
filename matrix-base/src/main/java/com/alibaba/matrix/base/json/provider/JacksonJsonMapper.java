package com.alibaba.matrix.base.json.provider;

import com.alibaba.matrix.base.json.JsonMapper;
import com.alibaba.matrix.base.util.T;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2024/9/1 15:47.
 */
@SuppressWarnings("all")
public class JacksonJsonMapper implements JsonMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        // 支持非标准json中增加//注释
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        // 该属性是用于关闭json key的常量化, 默认jackson会将json的key规范化(调用String#intern)用于减小消耗
        // 常见情况，将Map<String,Object>序列化为json
        // 但如果存在大量非固定key的json需要序列化和反序列化的时候，建议将该属性关闭
        // 如果主动开启该配置，需与 CANONICALIZE_FIELD_NAMES, true一起使用.
        objectMapper.getFactory().configure(JsonFactory.Feature.INTERN_FIELD_NAMES, false);
        // 该特性目的是提高jackson的性能，但有可能导致内存泄露，建议关闭.
        // 参考：https://github.com/FasterXML/jackson-core/issues/189
        objectMapper.getFactory().configure(JsonFactory.Feature.USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING, false);
        // 关闭未知属性错误异常，默认开启，建议关闭，对未知属性不处理
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // 属性为 null, 不序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


    @Override
    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return ExceptionUtils.rethrow(e);
        }
    }

    @Override
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            return ExceptionUtils.rethrow(e);
        }
    }

    @Override
    public <T> T fromJson(String json, Type type) {
        try {
            return (T) objectMapper.readValue(json, type2reference(type));
        } catch (JsonProcessingException e) {
            return ExceptionUtils.rethrow(e);
        }
    }

    @Override
    public <T> T fromJson(String json, TypeToken<T> typeToken) {
        try {
            return (T) objectMapper.readValue(json, type2reference(typeToken.getType()));
        } catch (JsonProcessingException e) {
            return ExceptionUtils.rethrow(e);
        }
    }

    private static final ConcurrentMap<Type, TypeReference<?>> TYPE_REFERENCE_MAP = new ConcurrentHashMap<>();

    private static TypeReference<?> type2reference(Type type) {
        return TYPE_REFERENCE_MAP.computeIfAbsent(type, _K -> new TypeReference<TypeReference<T>>() {
            @Override
            public Type getType() {
                return type;
            }
        });
    }
}
