package com.alibaba.matrix.extension.test;

import com.alibaba.matrix.extension.ExtensionContext;
import com.alibaba.matrix.extension.ExtensionInvoker;
import com.alibaba.matrix.extension.exception.ExtensionRuntimeException;
import com.alibaba.matrix.extension.reducer.Reducers;
import com.alibaba.matrix.extension.test.domain.TestModel;
import com.alibaba.matrix.extension.test.ext.DemoRemoteExt;
import com.google.common.base.Preconditions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.function.TriFunction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/16 14:10.
 */
@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-*.xml")
public class BaseExtensionTestCase {

    private static final Executor executor1 = Executors.newFixedThreadPool(1);

    private static final Executor executor2 = Executors.newFixedThreadPool(1);

    private final TestModel model = new TestModel();

    {
        model.name = "feiqing";
        model.list = Collections.singletonList(18);
        model.map = Collections.singletonMap("key", "value-101791");
    }

    /**
     * 基础功能
     */
    @Test
    public void test_base_impl() {
        Object first = ExtensionInvoker.invoke("code.base", TriFunction.class, ext -> ext.apply("arg1", model.list, null));
        System.out.println(first);
        Assert.assertTrue(String.valueOf(first).startsWith("BaseTriFunctionImpl"));
    }

    @Test
    public void test_code_object() {
        Object first = ExtensionInvoker.invoke("code.object", TriFunction.class, ext -> ext.apply("arg1", model.list, null));
        System.out.println(first);
        Assert.assertTrue(String.valueOf(first).contains("OBJ-TEST"));
    }

    @Test
    public void test_code_common() {
        Object first = ExtensionInvoker.invoke("code.common", TriFunction.class, ext -> ext.apply("arg1", model.list, null));
        System.out.println(first);
        Assert.assertTrue(String.valueOf(first).startsWith("CommonTriFunctionExtImpl"));
    }

    @Test
    public void test_code_yhb() {
        ExtensionContext.addExtCtx("ctx", "yhb");
        Object first = ExtensionInvoker.invoke("code.yhb", TriFunction.class, ext -> ext.apply("arg1", model.list, null));
        System.out.println(first);
        Assert.assertTrue(String.valueOf(first).startsWith("YhbTriFunctionExtImpl"));
    }

    @Test
    public void test_multi_thread_context() throws InterruptedException {
        AtomicReference<Object> first1 = new AtomicReference<>();
        AtomicReference<Object> first2 = new AtomicReference<>();
        executor1.execute(() -> {
            ExtensionContext.addExtCtx("ctx", "yhb");
            first1.set(ExtensionInvoker.invoke("code.yhb", TriFunction.class, ext -> ext.apply("arg1", model.list, null)));
        });
        executor2.execute(() -> first2.set(ExtensionInvoker.invoke("code.yhb", TriFunction.class, ext -> ext.apply("arg1", model.list, null))));

        Thread.sleep(1000L);
        System.out.println(first1);
        System.out.println(first2);

        Assert.assertEquals(first1.get(), first2.get());
    }

    /**
     * Sub scope
     */
    @Test
    public void test_base_scope_count() {
        ExtensionContext.addExtCtx("ctx", "BASE-SCOPE");
        Long count = ExtensionInvoker.invoke("code.sub.scope", TriFunction.class, ext -> ext.apply("arg1", model.list, null), Reducers.count());
        Assert.assertEquals(count.intValue(), 2);
    }

    @Test
    public void test_base_scope_impl() {
        ExtensionContext.addExtCtx("ctx", "BASE-SCOPE");
        List<Object> results = ExtensionInvoker.invoke("code.sub.scope", TriFunction.class, ext -> ext.apply("arg1", model.list, null), Reducers.collect());
        Assert.assertEquals(results.size(), 2);
        Assert.assertTrue(results.stream().allMatch(result -> String.valueOf(result).startsWith("SubScopeTriFunctionExtImpl")));
    }

    @Test
    public void test_sub_scope_count() {
        ExtensionContext.addExtCtx("ctx", "SUB-SCOPE");
        Long count = ExtensionInvoker.invoke("SUB-SCOPE", "code.sub.scope", TriFunction.class, ext -> ext.apply("arg1", model.list, null), Reducers.count());
        Assert.assertEquals(count.intValue(), 1);
    }

    @Test
    public void test_sub_scope_impl() {
        ExtensionContext.addExtCtx("ctx", "SUB-SCOPE");
        List<Object> results = ExtensionInvoker.invoke("SUB-SCOPE", "code.sub.scope", TriFunction.class, ext -> ext.apply("arg1", model.list, null), Reducers.collect());
        Assert.assertEquals(results.size(), 1);
        Assert.assertTrue(results.stream().allMatch(result -> String.valueOf(result).startsWith("SubScopeTriFunctionExtImpl")));
    }

    @Test
    public void test_base_vs_sub_scope() {
        ExtensionContext.addExtCtx("ctx", "BASE-SCOPE");
        Long baseCount = ExtensionInvoker.invoke("code.sub.scope", TriFunction.class, ext -> ext.apply("arg1", model.list, null), Reducers.count());

        ExtensionContext.addExtCtx("ctx", "SUB-SCOPE");
        Long subCount = ExtensionInvoker.invoke("SUB-SCOPE", "code.sub.scope", TriFunction.class, ext -> ext.apply("arg1", model.list, null), Reducers.count());

        Assert.assertNotEquals(baseCount, subCount);
    }

    /**
     * concurrent
     */
    @Test
    public void test_parallel_base() {
        for (int i = 0; i < 100; ++i) {
            List<Object> collect = ExtensionInvoker.invoke("code.normal.concurrent", TriFunction.class, ext -> ext.apply("arg1", model, null), Reducers.collect());
            Assert.assertEquals(4, collect.size());
        }
    }

    @Test
    public void test_parallel_context() {
        ExtensionContext.addExtCtx(model);
        Object result = ExtensionInvoker.invoke("code.normal.concurrent", TriFunction.class, ext -> {
            System.out.println(Thread.currentThread().getName());
            return ExtensionContext.getExtCtx(TestModel.class).list;
        }, Reducers.any());
        Assert.assertTrue(CollectionUtils.isEqualCollection(model.list, (Collection<?>) result));
    }


    @Test
    public void test_parallel_any() {
        ExtensionContext.addExtCtx("_ctx", "extension-context-value");
        ThreadLocal<String> normalContext = new ThreadLocal<>();
        normalContext.set("normal-test-value");

        Object any = ExtensionInvoker.invoke("code.normal.concurrent", TriFunction.class, ext -> {
            System.out.println(Thread.currentThread().getName() + " normalLocal = " + normalContext.get());
            return ext.apply("arg1", model, null);
        }, Reducers.any());
        Assert.assertTrue(String.valueOf(any).startsWith("NormalTriFunctionExtImpl"));
    }

    @Test
    public void test_parallel_nest() throws Throwable {
        List<String> collect = ExtensionInvoker.invoke("SUB-SCOPE", "code.sub.scope", TriFunction.class, unused -> {
            System.out.println(Thread.currentThread().getName());
            return ExtensionInvoker.invoke("code.sub.scope", TriFunction.class, ext -> {
                return "Hello: " + ext.apply("arg1", model, null);
            }, Reducers.collect());
        }, Reducers.any());
        Assert.assertEquals(2, collect.size());
        Assert.assertTrue(collect.stream().allMatch(str -> str.startsWith("Hello")));
    }

    @Test(expected = TimeoutException.class)
    public void test_parallel_nest_and_timeout() throws Throwable {
        try {
            ExtensionInvoker.invoke("code.normal.concurrent", TriFunction.class, unused -> {
                System.out.println(Thread.currentThread().getName());
                return ExtensionInvoker.invoke("code.normal.concurrent", TriFunction.class, ext -> {
                    return ext.apply("arg1", model, null);
                }, Reducers.any());
            }, Reducers.collect());
        } catch (ExtensionRuntimeException e) {
            throw e.getCauses()[0];
        }
    }

    @Test(expected = ExtensionRuntimeException.class)
    public void test_parallel_throwable() {
        try {
            ExtensionInvoker.invoke("code.normal.concurrent", TriFunction.class, ext -> {
                System.out.println(Thread.currentThread().getName());
                if (StringUtils.equals(Thread.currentThread().getName(), "E-Parallel-Testing-Exec-2")) {
                    throw new RuntimeException("TTT: " + Thread.currentThread().getName());
                }
                return ext.apply("arg1", model.list, null);
            }, Reducers.collect());
        } catch (ExtensionRuntimeException e) {
            e.printStackTrace();
            Assert.assertTrue(ArrayUtils.isNotEmpty(e.getCauses()));
            throw e;
        }
    }

    /**
     * todo test?
     */

    @Test
    public void test_parallel_any_thread_name() {
        Object result = ExtensionInvoker.invoke("code.normal.concurrent", TriFunction.class, ext -> {
            // Utils.sleep(100);
            return ExtensionInvoker.invoke("code.normal.concurrent", TriFunction.class, ext2 -> {
                //  Utils.sleep(100);
                return ExtensionInvoker.invoke("code.normal.concurrent", TriFunction.class, ext3 -> {
                    //  Utils.sleep(100);
                    return ExtensionInvoker.invoke("code.normal.concurrent", TriFunction.class, ext4 -> {
                        //  Utils.sleep(100);
                        System.out.println(Thread.currentThread().getName());
                        return Thread.currentThread().getName();
                    }, Reducers.any());
                }, Reducers.any());
            }, Reducers.any());
        }, Reducers.any());
        Assert.assertTrue(StringUtils.startsWith(String.valueOf(result), "E-Parallel-Common-Exec-"));
    }
}
