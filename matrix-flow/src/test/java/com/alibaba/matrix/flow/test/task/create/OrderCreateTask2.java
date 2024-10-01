package com.alibaba.matrix.flow.test.task.create;

import com.alibaba.matrix.flow.Task;
import com.alibaba.matrix.flow.test.model.OrderCreateContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/5/31 16:42.
 */
@Slf4j
public class OrderCreateTask2 implements Task<OrderCreateContext, Long> {
    @Override
    public Long execute(OrderCreateContext context) {
        log.info("OrderCreateNode2");

        return next();
    }
}
