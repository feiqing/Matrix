package com.alibaba.matrix.extension.test;

import com.alibaba.matrix.extension.ExtensionInvoker;
import com.alibaba.matrix.extension.test.ext.DemoShowExt;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2023/9/9 17:11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-*.xml")
public class DemoExtensionTestCase {

    @Test
    public void test_base_impl() {
        Object arg = "demo-arg";
        Object result = ExtensionInvoker.invoke("code.base.none.impl", DemoShowExt.class, ext -> ext.show(arg));
        Assert.assertNull(result);
    }

    @Test
    public void test_code_a() {
        Object arg = "demo-arg";

        String desc = ExtensionInvoker.invoke("code.a", DemoShowExt.class, DemoShowExt::desc);
        System.out.println(desc);

        Object result = ExtensionInvoker.invoke("code.a", DemoShowExt.class, ext -> ext.show(arg));
        Assert.assertNotNull(result);
        Assert.assertTrue(String.valueOf(result).startsWith("CodeA"));
    }

    @Test
    public void test_code_b() {
        Object arg = "demo-arg";

        String desc = ExtensionInvoker.invoke("code.b", DemoShowExt.class, DemoShowExt::desc);
        System.out.println(desc);

        Object result = ExtensionInvoker.invoke("code.b", DemoShowExt.class, ext -> ext.show(arg));
        Assert.assertNotNull(result);
        Assert.assertTrue(String.valueOf(result).startsWith("CodeB"));
    }

}
