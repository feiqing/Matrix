package com.alibaba.matrix.base.serializer;

/**
 * The Serializer interface provides methods to serialize Java objects to byte arrays and deserialize byte arrays back to Java objects.
 *
 * @author zhoupan@vdian.com
 * @since 2016/7/8.
 */
public interface Serializer {

    /**
     * Serializes a Java object to a byte array.
     *
     * @param obj the Java object to be serialized
     * @param <T> the type of the Java object
     * @return the byte array representation of the object
     */
    <T> byte[] serialize(T obj);

    /**
     * Deserializes a byte array back to a Java object of the specified type.
     *
     * @param bytes the byte array to be deserialized
     * @param <T>   the type of the resulting Java object
     * @return the Java object of the specified type
     */
    <T> T deserialize(byte[] bytes);
}
