package com.alibaba.matrix.extension.test.impl.function;

import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2024/7/25 22:45.
 */
@Component
public class BaseBiFunctionImpl implements BiFunction {

    @Override
    public Object apply(Object object, Object object2) {
        return "BaseBiFunctionImpl" + object + ", " + object2;
    }
}
