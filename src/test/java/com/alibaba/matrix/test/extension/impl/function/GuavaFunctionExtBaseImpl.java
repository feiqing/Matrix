package com.alibaba.matrix.test.extension.impl.function;

import com.alibaba.matrix.extension.annotation.ExtensionBase;
import com.google.common.base.Function;

import javax.annotation.Nullable;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2022/7/21 21:19.
 */
@ExtensionBase
public class GuavaFunctionExtBaseImpl implements Function<Object, Object> {

    @Nullable
    @Override
    public Object apply(@Nullable Object input) {
        return "GuavaFunctionExtBaseImpl: " + input;
    }
}
