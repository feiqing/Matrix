package com.alibaba.matrix.extension.test.impl.function;

import com.alibaba.matrix.extension.test.impl.base.BaseFunctionExtImpl;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2023/8/11 23:28.
 */
@Component
public class YhbFunctionExtImpl extends BaseFunctionExtImpl {

    @Override
    public Object apply(Object arg) {
        if (arg != null) {
            System.out.println("argType: " + arg.getClass());
        }
        return "YhbFunctionImpl apply(arg)";
    }
}
