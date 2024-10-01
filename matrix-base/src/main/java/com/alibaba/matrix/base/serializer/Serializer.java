package com.alibaba.matrix.base.serializer;

/**
 * @author zhoupan@vdian.com
 * @since 2016/7/8.
 */
public interface Serializer {

    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] bytes);
}
