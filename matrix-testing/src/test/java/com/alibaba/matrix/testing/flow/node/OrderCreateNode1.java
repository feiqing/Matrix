package com.alibaba.matrix.testing.flow.node;

import com.alibaba.matrix.flow.Task;
import com.alibaba.matrix.testing.flow.OrderCreateContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/5/31 16:22.
 */
@Slf4j
public class OrderCreateNode1 implements Task<OrderCreateContext, Long> {

    @Override
    public Long execute(OrderCreateContext context) {
        log.info("itemId = {}", context.getItemId());

        return next();
    }
}
