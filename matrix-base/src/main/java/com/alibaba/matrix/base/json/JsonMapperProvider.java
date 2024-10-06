package com.alibaba.matrix.base.json;

import com.alibaba.matrix.base.json.provider.FastJsonMapper;
import com.alibaba.matrix.base.util.MatrixServiceLoader;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2018/8/30 11:35.
 */
public class JsonMapperProvider {

    public static JsonMapper jsonMapper;

    static {
        jsonMapper = MatrixServiceLoader.loadService(JsonMapper.class, new FastJsonMapper());
    }
}
