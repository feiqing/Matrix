package com.alibaba.matrix.extension.test.impl.function;

import org.springframework.stereotype.Component;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 23:28.
 */
@Component
public class YhbFunctionImpl extends BaseFunctionImpl {

    @Override
    public Object apply(Object arg) {
        if (arg != null) {
            System.out.println("argType: " + arg.getClass());
        }
        return "YhbFunctionImpl apply(arg)";
    }
}
