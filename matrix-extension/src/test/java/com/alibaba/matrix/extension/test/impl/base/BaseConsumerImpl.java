package com.alibaba.matrix.extension.test.impl.base;

import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2023/9/18 17:48.
 */
@Component
public class BaseConsumerImpl implements Consumer<Object> {

    @Override
    public void accept(Object o) {
        if (o != null) {
            System.out.println("argType: " + o.getClass());
        }
    }
}
