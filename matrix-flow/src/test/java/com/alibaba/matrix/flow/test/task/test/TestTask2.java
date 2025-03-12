package com.alibaba.matrix.flow.test.task.test;

import com.alibaba.matrix.flow.Flow;
import com.alibaba.matrix.flow.Task;
import com.alibaba.matrix.flow.test.model.OrderCreateContext;

import javax.annotation.Resource;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2023/9/9 21:27.
 */
public class TestTask2 implements Task<OrderCreateContext, Void> {

    @Resource(name = "ORDER-CREATE-FLOW")
    private Flow<OrderCreateContext, Long> flow;

    @Override
    public Void execute(OrderCreateContext orderCreateContext) {
        Long execute = flow.execute(orderCreateContext);
        System.out.println("TestTask2: " + execute);
        return next();
    }
}
