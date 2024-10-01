package com.alibaba.matrix.extension.test.impl.base;

import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 23:26.
 */
@Component
public class BaseSupplierExtImpl implements Supplier<Object> {

    @Override
    public Object get() {
        return "BaseSupplierExtImpl.get()";
    }
}
