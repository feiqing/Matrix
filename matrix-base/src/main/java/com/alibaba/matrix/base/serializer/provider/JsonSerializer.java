package com.alibaba.matrix.base.serializer.provider;

import com.alibaba.matrix.base.serializer.Serializer;

import java.nio.charset.StandardCharsets;

import static com.alibaba.matrix.base.json.JsonMapperProvider.jsonMapper;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2018-07-01 21:41:00.
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
