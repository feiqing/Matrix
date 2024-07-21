package com.alibaba.matrix.test.extension.impl.function;

import com.alibaba.matrix.extension.annotation.ExtensionImpl;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 23:29.
 */
@ExtensionImpl(code = "yhb")
public class YhbSupplierImpl extends BaseSupplierImpl {
    @Override
    public Object get() {
        return "YhbSupplierImpl get()";
    }
}
