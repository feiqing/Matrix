package com.alibaba.matrix.testing.flow.node;

import com.alibaba.matrix.flow.FlowNode;
import com.alibaba.matrix.testing.flow.OrderCreateContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/5/31 16:22.
 */
@Slf4j
public class OrderCreateNode1 extends FlowNode<OrderCreateContext, Long> {

    @Override
    protected Long execute(OrderCreateContext context) throws Throwable {
        log.info("itemId = {}", context.getItemId());

        return next();
    }
}
