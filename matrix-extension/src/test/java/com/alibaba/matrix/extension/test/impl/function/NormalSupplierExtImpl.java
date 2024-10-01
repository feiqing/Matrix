package com.alibaba.matrix.extension.test.impl.function;

import com.alibaba.matrix.extension.annotation.ExtensionImpl;
import com.alibaba.matrix.extension.test.impl.base.BaseSupplierExtImpl;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/8 23:00.
 */
@ExtensionImpl(code = "normal")
public class NormalSupplierExtImpl extends BaseSupplierExtImpl {

    @Override
    public Object get() {
        return "NormalSupplierExtImpl.get()";
    }
}
