package com.alibaba.matrix.test.flow.node;

import com.alibaba.matrix.flow.FlowNode;
import com.alibaba.matrix.test.flow.OrderCreateContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/5/31 16:42.
 */
@Slf4j
public class OrderCreateNode2 extends FlowNode<OrderCreateContext, Long> {
    @Override
    protected Long execute(OrderCreateContext context) throws Throwable {
        log.info("OrderCreateNode2");

        return next();
    }
}
