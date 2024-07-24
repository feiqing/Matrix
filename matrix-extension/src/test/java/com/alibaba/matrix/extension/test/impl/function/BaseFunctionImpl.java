package com.alibaba.matrix.extension.test.impl.function;

import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 23:24.
 */
@Component
public class BaseFunctionImpl implements Function<Object, Object> {

    @Override
    public Object apply(Object arg) {
        if (arg != null) {
            System.out.println("argType: " + arg.getClass());
        }

        return "BaseFunctionImpl apply(arg)";
    }
}
