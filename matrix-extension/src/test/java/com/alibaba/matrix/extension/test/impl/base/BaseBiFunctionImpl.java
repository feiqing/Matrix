package com.alibaba.matrix.extension.test.impl.base;

import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/18 17:44.
 */
@Component
public class BaseBiFunctionImpl implements BiFunction<Object, Object, Object> {

    @Override
    public Object apply(Object arg1, Object arg2) {
        if (arg1 != null) {
            System.out.println("arg1Type: " + arg1.getClass());
        }
        if (arg2 != null) {
            System.out.println("arg2Type: " + arg2.getClass());
        }

        return "BaseBiFunctionImpl apply(" + arg1 + ", " + arg2 + ")";
    }
}
