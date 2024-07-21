package com.alibaba.matrix.test.extension;

import com.alibaba.matrix.extension.ExtensionContext;
import com.alibaba.matrix.extension.ExtensionInvoker;
import com.alibaba.matrix.extension.reducer.Reducers;
import com.alibaba.matrix.extension.spring.EnableExtension;
import com.alibaba.matrix.test.extension.ext.ShowDemoExt;
import com.google.common.base.Function;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Map;
import java.util.function.Supplier;

import static com.alibaba.matrix.extension.ExtensionInvoker.BASE_EXTENSION_ROUTER;
import static com.alibaba.matrix.test.extension.constants.ExtensionConstants.GROUP_1;
import static com.alibaba.matrix.test.extension.constants.ExtensionConstants.GROUP_2;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/16 14:10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@EnableExtension(enableAnnotationScan = true, scanPackages = "com.alibaba.matrix.test.extension")
@ContextConfiguration(locations = "classpath:spring-*.xml")
public class ExtensionTester {

    @Resource
    private Function<Object, Object> guavaFunctionExt;

    @Test
    public void test() throws Exception {
        try {
            ExtensionInvoker.resolve(String::toString, "no-code");

            Object invoke = ExtensionInvoker.invoke(ShowDemoExt.class, ShowDemoExt::showDemo, Reducers.firstOf());
            System.out.println("invoke = " + invoke);

            new Thread(() -> {
                ExtensionInvoker.resolve(String::toString, "my-code-1");
                Object invoke1 = ExtensionInvoker.invoke(ShowDemoExt.class, ShowDemoExt::showDemo, Reducers.firstOf());
                System.out.println("invoke = " + invoke1);
            }).start();

            Thread.sleep(5000);
        } finally {
            ExtensionInvoker.clear();
        }
    }

    @Test
    public void testMultiThread() throws Exception {
        try {
            ExtensionInvoker.resolve(String::toString, "my-code-1");

            Object invoke = ExtensionInvoker.invoke(ShowDemoExt.class, ShowDemoExt::showDemo, Reducers.firstOf());
            System.out.println("main thread invoke = " + invoke);

            Map<String, Map<String, Serializable>> copyOfContextMap = ExtensionContext.getCopyOfContextMap();
            new Thread(() -> {
                ExtensionContext.setContextMap(copyOfContextMap);
                Object invoke1 = ExtensionInvoker.invoke(ShowDemoExt.class, ShowDemoExt::showDemo, Reducers.firstOf());
                System.out.println("sub thread invoke = " + invoke1);
                ExtensionContext.clear();
            }).start();

            invoke = ExtensionInvoker.invoke(ShowDemoExt.class, ShowDemoExt::showDemo, Reducers.firstOf());
            System.out.println("main thread  invoke = " + invoke);

            Thread.sleep(5000);
        } finally {
            ExtensionInvoker.clear();
        }
    }

    @Test
    public void testSubGroup1() {
        try {
            ExtensionInvoker.resolve(_k -> "base-code", null);

            Object baseInvoke = ExtensionInvoker.invoke(ShowDemoExt.class, ShowDemoExt::showDemo);
            System.out.println(baseInvoke);

            // 二次解析 & 二次调用
            ExtensionInvoker.resolve(GROUP_1, _k -> "my-code-1", null);
            Object subGroupInvoke = ExtensionInvoker.invoke(GROUP_1, ShowDemoExt.class, ShowDemoExt::showDemo);
            System.out.println(subGroupInvoke);

            ExtensionInvoker.resolve(GROUP_2, _k -> "my-code-1", null);
            subGroupInvoke = ExtensionInvoker.invoke(GROUP_2, ShowDemoExt.class, ShowDemoExt::showDemo);
            System.out.println(subGroupInvoke);

            // 回到初次调用
            baseInvoke = ExtensionInvoker.invoke(ShowDemoExt.class, ShowDemoExt::showDemo);
            System.out.println(baseInvoke);
        } finally {
            ExtensionInvoker.clear();
        }
    }


    @Test
    public void testSubGroup() {
        try {
            ExtensionInvoker.resolve(_k -> "base-code", null);

            Object baseInvoke = ExtensionInvoker.invoke(Supplier.class, Supplier::get);
            System.out.println(baseInvoke);

            // 二次解析 & 二次调用
            ExtensionInvoker.resolve("SUB-GROUP", BASE_EXTENSION_ROUTER, _k -> "base-code", null);
            Object subGroupInvoke = ExtensionInvoker.invoke("SUB-GROUP", Function.class, function -> function.apply("yhb"));
            System.out.println(subGroupInvoke);

            // 回到初次调用
            baseInvoke = ExtensionInvoker.invoke(Supplier.class, Supplier::get);
            System.out.println(baseInvoke);
        } finally {
            ExtensionInvoker.clear();
        }
    }

    @Test
    public void testResource() {
        try {
            ExtensionInvoker.resolve(_k -> "base-code", null);

            guavaFunctionExt.apply("feiqing-test");
        } finally {
            ExtensionInvoker.clear();
        }
    }
}
