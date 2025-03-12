package com.alibaba.matrix.base.serializer;

import com.alibaba.matrix.base.serializer.provider.JdkSerializer;
import com.alibaba.matrix.base.util.MatrixServiceLoader;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2017/5/1 15:13.
 */
public class SerializerProvider {

    public static Serializer serializer;

    static {
        serializer = MatrixServiceLoader.loadService(Serializer.class, new JdkSerializer());
    }
}
