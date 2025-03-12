package com.alibaba.matrix.extension.test.impl.base;

import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2023/9/18 17:46.
 */
@Component
public class BaseBiConsumerImpl implements BiConsumer<Object, Object> {

    @Override
    public void accept(Object arg1, Object arg2) {
        if (arg1 != null) {
            System.out.println("arg1Type: " + arg1.getClass());
        }
        if (arg2 != null) {
            System.out.println("arg2Type: " + arg2.getClass());
        }
    }
}
