package com.alibaba.matrix.extension.test;

import com.alibaba.matrix.extension.ExtensionContext;
import com.alibaba.matrix.extension.ExtensionInvoker;
import com.alibaba.matrix.extension.exception.ExtensionRuntimeException;
import com.alibaba.matrix.extension.reducer.Reducers;
import com.alibaba.matrix.extension.test.domain.TestModel;
import com.alibaba.matrix.extension.test.ext.DemoRemoteExt;
import com.alibaba.matrix.extension.test.ext.DemoShowExt;
import com.google.common.base.Function;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.alibaba.matrix.extension.test.constants.ExtScopes.SCOPE_1;
import static com.alibaba.matrix.extension.test.constants.ExtScopes.SCOPE_2;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/16 14:10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-*.xml")
public class TestExtensionTestCase {

    @Test
    public void tt() {
        Boolean hah = ExtensionInvoker.invoke("code.tree", Bag.class, bag -> bag.add("hah", 4));
        System.out.println(hah);
        Integer hah1 = ExtensionInvoker.invoke("code.hash2", Bag.class, bag -> bag.getCount("hah"));
        System.out.println(hah1);
    }

    @Test
    public void test() throws Exception {
        Object invoke = ExtensionInvoker.invoke("no-code", DemoShowExt.class, DemoShowExt::show, Reducers.first());
        System.out.println("invoke = " + invoke);

        new Thread(() -> {
            Object invoke1 = ExtensionInvoker.invoke(SCOPE_2, "my-code-1", DemoShowExt.class, DemoShowExt::show, Reducers.first());
            System.out.println("invoke = " + invoke1);
        }).start();
    }

    @Test
    public void testMultiThread() throws Exception {
        Object invoke = ExtensionInvoker.invoke("my-code-1", DemoShowExt.class, DemoShowExt::show, Reducers.first());
        System.out.println("main thread invoke = " + invoke);

        new Thread(() -> {
            Object invoke1 = ExtensionInvoker.invoke("", DemoShowExt.class, DemoShowExt::show, Reducers.first());
            System.out.println("sub thread invoke = " + invoke1);
        }).start();

        invoke = ExtensionInvoker.invoke("my-code-1", DemoShowExt.class, DemoShowExt::show, Reducers.first());
        System.out.println("main thread  invoke = " + invoke);
    }

    @Test
    public void testSubScope1() {
        Object baseInvoke = ExtensionInvoker.invoke("base-code", DemoShowExt.class, DemoShowExt::show);
        System.out.println(baseInvoke);

        // 二次解析 & 二次调用
        Object subScopeInvoke = ExtensionInvoker.invoke(SCOPE_1, "my-code-1", DemoShowExt.class, DemoShowExt::show);
        System.out.println(subScopeInvoke);

        subScopeInvoke = ExtensionInvoker.invoke(SCOPE_2, "my-code-1", DemoShowExt.class, DemoShowExt::show);
        System.out.println(subScopeInvoke);

        // 回到初次调用
        baseInvoke = ExtensionInvoker.invoke("my-code-1", DemoShowExt.class, DemoShowExt::show);
        System.out.println(baseInvoke);
    }


    @Test
    public void testSubScope() {
        Object baseInvoke = ExtensionInvoker.invoke("base-code", Supplier.class, Supplier::get);
        System.out.println(baseInvoke);

        // 二次解析 & 二次调用
        Object subScopeInvoke = ExtensionInvoker.invoke("SUB-SCOPE", "test", Function.class, function -> function.apply("yhb"));
        System.out.println(subScopeInvoke);

        // 回到初次调用
        baseInvoke = ExtensionInvoker.invoke("test", Supplier.class, Supplier::get);
        System.out.println(baseInvoke);


        List<Object> hhh = ExtensionInvoker.invoke("SUB-SCOPE", Function.class, function -> function.apply("hhh"), Reducers.collect());
        System.out.println(hhh);
    }

    private final TestModel model = new TestModel();

    {
        model.name = "feiqing";
        model.list = Collections.singletonList(18);
        model.map = Collections.singletonMap("key", "value-101791");
    }

    @Test
    public void test_parallel_any() {
        Object result = ExtensionInvoker.invoke("code.http", DemoRemoteExt.class, ext -> ext.apply("arg1", model.list, null), Reducers.any());
        Assert.assertTrue(CollectionUtils.isEqualCollection(model.list, (Collection<?>) result));
    }

    @Test
    public void test_parallel_any_thread_name() {
        Object result = ExtensionInvoker.invoke("code.http", DemoRemoteExt.class, ext -> {
            try {
                TimeUnit.MILLISECONDS.sleep(RandomUtils.nextInt(0, 500));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return Thread.currentThread().getName();
        }, Reducers.any());
        Assert.assertTrue(StringUtils.startsWith(String.valueOf(result), "E-Parallel-Common-Exec-"));
    }

    @Test
    public void test_parallel_collect() {
        List<Object> results = ExtensionInvoker.invoke("code.http", DemoRemoteExt.class, ext -> {
            System.out.println(Thread.currentThread().getName());
            return ext.apply("arg1", model.list, null);
        }, Reducers.collect());
        Assert.assertEquals(CollectionUtils.size(results), 6);
    }

    @Test
    public void test_parallel_context() {
        ExtensionContext.addExtCtx(model);
        Object result = ExtensionInvoker.invoke("code.http", DemoRemoteExt.class, ext -> {
            System.out.println(Thread.currentThread().getName());
            return ExtensionContext.getExtCtx(TestModel.class).list;
        }, Reducers.any());
        Assert.assertTrue(CollectionUtils.isEqualCollection(model.list, (Collection<?>) result));
    }

    @Test(expected = ExtensionRuntimeException.class)
    public void test_parallel_throwable() {
        Object result = ExtensionInvoker.invoke("code.http", DemoRemoteExt.class, ext -> {
            System.out.println(Thread.currentThread().getName());
            if (StringUtils.equals(Thread.currentThread().getName(), "E-Parallel-Common-Exec-3")) {
                throw new RuntimeException("TTT: " + Thread.currentThread().getName());
            }
            if (StringUtils.equals(Thread.currentThread().getName(), "E-Parallel-Common-Exec-4")) {
                throw new RuntimeException("TTT: " + Thread.currentThread().getName());
            }
            return ext.apply("arg1", model.list, null);
        }, Reducers.any());
        Assert.assertTrue(CollectionUtils.isEqualCollection(model.list, (Collection<?>) result));
    }
}
