package com.alibaba.matrix.extension.test.impl.function;

import com.alibaba.matrix.extension.ExtensionContext;
import org.apache.commons.lang3.function.TriFunction;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2023/9/18 20:09.
 */
@Component
public class NormalTriFunctionExtImpl implements TriFunction<Object, Object, Object, Object> {

    @Override
    public Object apply(Object o, Object o2, Object o3) {
        System.out.println("NormalTriFunctionExtImpl thread = " + Thread.currentThread().getName() + ", extensionLocal = " + ExtensionContext.getExtCtx("_ctx") + ", args: " + o + ", " + o2 + ", " + o3);
        return "NormalTriFunctionExtImpl(" + o + ", " + o2 + ", " + o3 + ")";
    }
}
