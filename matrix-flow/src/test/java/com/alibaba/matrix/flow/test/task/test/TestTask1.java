package com.alibaba.matrix.flow.test.task.test;


import com.alibaba.matrix.flow.Task;
import com.alibaba.matrix.flow.test.model.OrderCreateContext;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/4/10 15:59.
 */
public class TestTask1 implements Task<OrderCreateContext, Void> {

    @Override
    public Void execute(OrderCreateContext orderCreateContext) {
        System.out.println("TestTask1");
        return next();
    }
}
