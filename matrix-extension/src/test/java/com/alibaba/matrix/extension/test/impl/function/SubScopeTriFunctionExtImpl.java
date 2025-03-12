package com.alibaba.matrix.extension.test.impl.function;

import com.alibaba.matrix.extension.ExtensionContext;
import com.alibaba.matrix.extension.test.impl.base.BaseTriFunctionImpl;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2023/9/18 20:29.
 */
@Component
public class SubScopeTriFunctionExtImpl extends BaseTriFunctionImpl {

    @Override
    public Object apply(Object o, Object o2, Object o3) {
        System.out.println("SubScopeTriFunctionExtImpl thread = " + Thread.currentThread().getName() + ", ctx = " + ExtensionContext.getExtCtx("ctx") + ", args: " + o + ", " + o2 + ", " + o3);
        return "SubScopeTriFunctionExtImpl(" + o + ", " + o2 + ", " + o3 + ")";
    }
}
