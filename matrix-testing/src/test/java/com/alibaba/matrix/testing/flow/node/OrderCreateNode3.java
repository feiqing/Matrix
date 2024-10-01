package com.alibaba.matrix.testing.flow.node;

import com.alibaba.matrix.flow.Task;
import com.alibaba.matrix.testing.flow.OrderCreateContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/5/31 16:41.
 */
@Slf4j
public class OrderCreateNode3 implements Task<OrderCreateContext, Long> {

    @Override
    public Long execute(OrderCreateContext context) {

        log.info("OrderCreateNode3...");

        return new Random().nextLong();

    }
}
