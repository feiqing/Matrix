package com.alibaba.matrix.testing;

import com.alibaba.matrix.extension.ExtensionContext;
import com.alibaba.matrix.extension.ExtensionInvoker;
import com.alibaba.matrix.extension.reducer.Reducers;
import com.alibaba.matrix.extension.spring.EnableExtension;
import com.alibaba.matrix.flow.Flow;
import com.alibaba.matrix.testing.extension.ext.ShowDemoExt;
import com.alibaba.matrix.testing.flow.OrderCreateContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Map;
import java.util.Random;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2024/7/24 23:05.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@EnableExtension(enableAnnotationScan = true, scanPackages = "com.alibaba.matrix.test.extension")
@ContextConfiguration(locations = "classpath:spring-*.xml")
public class IntegrationTester {

    @Resource
    private Flow<OrderCreateContext, Long> flow;

    @Test
    public void test() throws Exception {
        try {
            ExtensionInvoker.resolve(String::toString, "my-code-1");

            Object invoke = ExtensionInvoker.invoke(ShowDemoExt.class, ShowDemoExt::showDemo, Reducers.firstOf());
            System.out.println("main thread invoke = " + invoke);

            OrderCreateContext input = new OrderCreateContext();
            input.setItemId(new Random().nextInt());
            long bizOrderId = flow.execute(input);
            System.out.println("bizOrderId = " + bizOrderId);

            Thread.sleep(5000);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            ExtensionInvoker.clear();
        }
    }
}
