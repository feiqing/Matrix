package com.alibaba.matrix.base.serializer.provider;

import com.alibaba.matrix.base.serializer.Serializer;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2018-05-02 23:47:00.
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
