package com.alibaba.matrix.flow.test.task.create;

import com.alibaba.matrix.flow.Task;
import com.alibaba.matrix.flow.test.model.OrderCreateContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/5/31 16:22.
 */
@Slf4j
public class OrderCreateTask1 implements Task<OrderCreateContext, Long> {

    @Override
    public Long execute(OrderCreateContext context) {
        log.info("itemId = {}", context.getItemId());

        return next();
    }
}
