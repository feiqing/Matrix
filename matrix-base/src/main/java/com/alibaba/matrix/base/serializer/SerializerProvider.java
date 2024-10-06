package com.alibaba.matrix.base.serializer;

import com.alibaba.matrix.base.serializer.provider.Hessian2Serializer;
import com.alibaba.matrix.base.util.MatrixServiceLoader;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2017/5/1 15:13.
 */
public class SerializerProvider {

    public static Serializer serializer;

    static {
        serializer = MatrixServiceLoader.loadService(Serializer.class, new Hessian2Serializer());
    }
}
