package com.alibaba.matrix.testing.flow.node;

import com.alibaba.matrix.flow.Task;
import com.alibaba.matrix.testing.flow.OrderCreateContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/5/31 16:42.
 */
@Slf4j
public class OrderCreateNode2 implements Task<OrderCreateContext, Long> {
    @Override
    public Long execute(OrderCreateContext context) {
        log.info("OrderCreateNode2");

        return next();
    }
}
