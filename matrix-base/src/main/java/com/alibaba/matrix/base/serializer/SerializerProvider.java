package com.alibaba.matrix.base.serializer;

import com.alibaba.matrix.base.serializer.provider.Hessian2Serializer;
import com.alibaba.matrix.base.util.MatrixServiceLoader;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/1 15:13.
 */
public class SerializerProvider {

    public static Serializer serializer;

    static {
        serializer = MatrixServiceLoader.loadService(Serializer.class, new Hessian2Serializer());
    }
}
