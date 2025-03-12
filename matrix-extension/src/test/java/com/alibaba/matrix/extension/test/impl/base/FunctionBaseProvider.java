package com.alibaba.matrix.extension.test.impl.base;

import java.util.function.Function;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2024/10/10 15:23.
 */
public class FunctionBaseProvider {

    public static Object newFunction() {
        return (Function<Object, Object>) o -> {
            Object result = "FunctionBaseProvider.new.function.base.impl: " + o;
            System.out.println(result);
            return result;
        };
    }
}
