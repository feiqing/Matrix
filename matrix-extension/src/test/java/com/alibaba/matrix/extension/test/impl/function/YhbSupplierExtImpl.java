package com.alibaba.matrix.extension.test.impl.function;

import com.alibaba.matrix.extension.test.impl.base.BaseSupplierExtImpl;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2023/8/11 23:29.
 */
@Component
public class YhbSupplierExtImpl extends BaseSupplierExtImpl {
    @Override
    public Object get() {
        return "YhbSupplierExtImpl.get()";
    }
}
