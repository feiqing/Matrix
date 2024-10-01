package com.alibaba.matrix.base.serializer.provider;

import com.alibaba.matrix.base.serializer.Serializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author jifang.zjf
 * @since 2017/6/19 下午4:53.
 */
public class KryoSerializer implements Serializer {

    private static final ThreadLocal<Kryo> kryo = ThreadLocal.withInitial(() -> {
        Kryo k = new Kryo();
        k.setRegistrationRequired(false);
        return k;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             Output output = new Output(bos)) {
            kryo.get().writeClassAndObject(output, obj);
            output.flush();

            return bos.toByteArray();
        } catch (IOException e) {
            return ExceptionUtils.rethrow(e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) {
        try (Input input = new Input(bytes)) {
            return kryo.get().readClassAndObject(input);
        }
    }
}
