package com.alibaba.matrix.extension.test.impl.function;

import com.alibaba.matrix.extension.annotation.ExtensionImpl;
import com.alibaba.matrix.extension.test.constants.ExtScopeDef;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 23:29.
 */
@ExtensionImpl(scope = ExtScopeDef.TEST, code = "yhb")
public class YhbSupplierImpl extends BaseSupplierImpl {
    @Override
    public Object get() {
        return "YhbSupplierImpl get()";
    }
}
