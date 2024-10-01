package com.alibaba.matrix.extension.test.impl.base;

import org.apache.commons.lang3.function.TriFunction;
import org.springframework.stereotype.Component;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/18 17:42.
 */
@Component
public class BaseTriFunctionImpl implements TriFunction<Object, Object, Object, Object> {

    @Override
    public Object apply(Object o, Object o2, Object o3) {
        if (o != null) {
            System.out.println("arg1Type: " + o.getClass());
        }
        if (o2 != null) {
            System.out.println("arg2Type: " + o2.getClass());
        }
        if (o3 != null) {
            System.out.println("arg2Type: " + o3.getClass());
        }

        return "BaseTriFunctionImpl apply(" + o + ", " + o2 + ", " + o3 + ")";
    }
}
