package com.alibaba.matrix.extension.test.impl.function;

import com.alibaba.matrix.extension.annotation.ExtensionImpl;
import com.alibaba.matrix.extension.test.impl.base.BaseSupplierExtImpl;
import org.springframework.stereotype.Component;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/8 23:00.
 */
@Component
@ExtensionImpl(code = "normal")
public class NormalSupplierExtImpl extends BaseSupplierExtImpl {

    @Override
    public Object get() {
        return "NormalSupplierExtImpl.get()";
    }
}
