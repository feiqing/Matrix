package com.alibaba.matrix.base.serializer.provider;

import com.alibaba.matrix.base.serializer.Serializer;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

/**
 * @author zhujifang@vdian.com
 * @version 1.0
 * @since 2016/7/2 下午5:00.
 */
public class JdkSerializer implements Serializer {

    @Override
    public byte[] serialize(Object obj) {
        return SerializationUtils.serialize((Serializable) obj);
    }

    @Override
    public Object deserialize(byte[] bytes) {
        return SerializationUtils.deserialize(bytes);
    }
}
