package com.alibaba.matrix.base.serializer.provider;

import com.alibaba.matrix.base.serializer.Serializer;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author zhujifang@vdian.com
 * @version 1.0
 * @since 2016/7/2 下午5:00.
 */
public class JdkGzipSerializer implements Serializer {

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPOutputStream gzout = new GZIPOutputStream(bos);
             ObjectOutputStream out = new ObjectOutputStream(gzout)) {
            out.writeObject(obj);
            return bos.toByteArray();
        } catch (IOException e) {
            return ExceptionUtils.rethrow(e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             GZIPInputStream gzin = new GZIPInputStream(bis);
             ObjectInputStream ois = new ObjectInputStream(gzin)) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return ExceptionUtils.rethrow(e);
        }
    }
}