package com.alibaba.matrix.testing;

import com.alibaba.matrix.extension.ExtensionInvoker;
import com.alibaba.matrix.extension.reducer.Reducers;
import com.alibaba.matrix.flow.Flow;
import com.alibaba.matrix.testing.extension.ext.ShowDemoExt;
import com.alibaba.matrix.testing.flow.OrderCreateContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Random;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2023/7/24 23:05.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-*.xml")
public class IntegrationTester {

    @Resource
    private Flow<OrderCreateContext, Long> flow;

    @Test
    public void test() throws Throwable {
        Object invoke = ExtensionInvoker.invoke("my-code-1", ShowDemoExt.class, ShowDemoExt::showDemo, Reducers.first());
        System.out.println("main thread invoke = " + invoke);

        OrderCreateContext input = new OrderCreateContext();
        input.setItemId(new Random().nextInt());
        long bizOrderId = flow.execute(input);
        System.out.println("bizOrderId = " + bizOrderId);

        Thread.sleep(5000);

    }
}
