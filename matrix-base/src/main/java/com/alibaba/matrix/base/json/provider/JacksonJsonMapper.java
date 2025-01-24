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
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.getFactory().configure(JsonFactory.Feature.INTERN_FIELD_NAMES, false);
        // Ref：https://github.com/FasterXML/jackson-core/issues/189
        objectMapper.getFactory().configure(JsonFactory.Feature.USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING, false);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
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
