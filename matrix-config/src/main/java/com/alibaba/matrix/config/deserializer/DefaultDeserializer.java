package com.alibaba.matrix.config.deserializer;

import com.alibaba.matrix.base.util.MatrixUtils;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2017/4/23 22:51.
 */
public class DefaultDeserializer implements Deserializer {

    @Override
    public Object deserialize(Context context) {
        return MatrixUtils.str2obj(context.getValueStr(), context.getType());
    }
}
