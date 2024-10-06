package com.alibaba.matrix.base.serializer.provider;

import com.alibaba.matrix.base.serializer.Serializer;

import java.nio.charset.StandardCharsets;

import static com.alibaba.matrix.base.json.JsonMapperProvider.jsonMapper;

/**
 * @author zhujifang@vdian.com
 * @version 1.0
 * @since 2016/7/2 下午5:00.
 */
public class JsonSerializer implements Serializer {

    private final Class<?> type;

    public JsonSerializer(Class<?> type) {
        this.type = type;
    }

    @Override
    public byte[] serialize(Object obj) {
        if (obj == null) {
            return null;
        }
        return jsonMapper.toJson(obj).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Object deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return jsonMapper.fromJson(new String(bytes, StandardCharsets.UTF_8), type);
    }
}
