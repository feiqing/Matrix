package com.alibaba.matrix.flow.test;

import com.alibaba.matrix.flow.Flow;
import com.alibaba.matrix.flow.test.model.OrderCreateContext;
import com.alibaba.matrix.flow.test.task.create.OrderCreateTask1;
import com.alibaba.matrix.flow.test.task.create.OrderCreateTask2;
import com.alibaba.matrix.flow.test.task.create.OrderCreateTask3;
import com.alibaba.matrix.flow.test.task.error.ErrorTask;
import com.alibaba.matrix.flow.test.task.error.NormalTask;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/5/31 11:56.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring-*.xml")
public class BaseFlowTestCase {

    @Resource(name = "ORDER-CREATE-FLOW")
    private Flow<OrderCreateContext, Long> orderCreateFlow;

    @Test
    public void test_order_create_flow() throws Throwable {
        OrderCreateContext input = new OrderCreateContext();
        input.setItemId(new Random().nextInt());
        long bizOrderId = orderCreateFlow.execute(input);
        System.out.println("bizOrderId = " + bizOrderId);
        Assert.assertTrue(bizOrderId > 0);
    }

    private Flow<OrderCreateContext, Long> dynamicCreateFlow;

    @Before
    public void setUp() {
        OrderCreateTask1 node1 = new OrderCreateTask1();
        OrderCreateTask2 node2 = new OrderCreateTask2();
        OrderCreateTask3 node3 = new OrderCreateTask3();
        dynamicCreateFlow = new Flow<>("ORDER-CREATE-2", Arrays.asList(node1, node1, node3, node2, node1));
    }

    @Test
    public void test_dynamic_create_flow() throws Throwable {
        long execute = dynamicCreateFlow.execute(new OrderCreateContext(ThreadLocalRandom.current().nextInt()));
        System.out.println(execute);
        Assert.assertTrue(execute > 0);
    }

    @Resource(name = "TEST-FLOW")
    private Flow<OrderCreateContext, Void> testFlow;

    @Test(expected = java.lang.IllegalStateException.class)
    public void test_dynamic_create_flow_task() throws Throwable {
        OrderCreateContext input = new OrderCreateContext();
        input.setItemId(new Random().nextInt());
        Void execute = testFlow.execute(input);
        System.out.println(execute);
    }


    private Flow<Object, Object> xx;
    {
        xx = new Flow<>("xx", Arrays.asList(new NormalTask(), new ErrorTask()));
    }

    @Test
    public void xx(){
        Object execute = xx.execute("feiqing-teset");
        System.out.println(execute);
    }

}
