package com.alibaba.matrix.flow.test.task.test;

import com.alibaba.matrix.flow.Task;
import com.alibaba.matrix.flow.test.model.OrderCreateContext;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/9 21:28.
 */
public class TestTask3 implements Task<OrderCreateContext, Void> {

    @Override
    public Void execute(OrderCreateContext orderCreateContext) {
        System.out.println("TestTask3");
        return null;
    }
}
