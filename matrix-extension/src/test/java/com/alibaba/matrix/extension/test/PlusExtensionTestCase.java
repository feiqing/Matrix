package com.alibaba.matrix.extension.test;

import com.alibaba.matrix.extension.ExtensionInvoker;
import com.alibaba.matrix.extension.reducer.Reducers;
import com.alibaba.matrix.extension.test.domain.TestModel;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/8 22:10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-*.xml")
public class PlusExtensionTestCase {

    private final TestModel model = new TestModel();

    {
        model.name = "feiqing";
        model.list = Collections.singletonList(18);
        model.map = Collections.singletonMap("key", "value-101791");
    }

    @Test
    public void test_nest_invoke1() {
        ExtensionInvoker.invoke("normal", Supplier.class, ext -> {
            Object extResult1 = ext.get();

            Object subExtResult = ExtensionInvoker.invoke("SUB-SCOPE", "normal", Supplier.class, Supplier::get);
            Preconditions.checkState(StringUtils.startsWith(String.valueOf(subExtResult), "BaseSupplierExtImpl"));

            Object extResult2 = ext.get();
            Assert.assertEquals(extResult1, extResult2);
            Preconditions.checkState(StringUtils.startsWith(String.valueOf(extResult1), "NormalSupplierExtImpl"));

            return subExtResult;
        });
    }

    @Test
    public void test_nest_invoke2() {
        ExtensionInvoker.invoke("normal", Supplier.class, ext -> {

            Object extResult1 = ext.get();

            // 注意, 这里调用的是ext, 而非subExt
            Object extResult2 = ExtensionInvoker.invoke("SUB-SCOPE", "yhb", Supplier.class, subExt -> ext.get());
            Assert.assertEquals(extResult1, extResult2);

            Object subExtResult = ExtensionInvoker.invoke("SUB-SCOPE", "yhb", Supplier.class, Supplier::get);
            Assert.assertNotEquals(extResult2, subExtResult);

            return extResult1;
        });
    }

    @Test
    public void test_nest_invoke3() {
        Object result1 = ExtensionInvoker.invoke("normal", Supplier.class, Supplier::get);
        Object result2 = ExtensionInvoker.invoke("normal", Supplier.class, ext -> {
            return ExtensionInvoker.invoke("SUB-SCOPE", "normal", Supplier.class, Supplier::get);
        });
        Assert.assertNotEquals(result1, result2);
    }

    @Test
    public void test_result_process1() {
        Object result1 = ExtensionInvoker.invoke("SUB-SCOPE", "normal", Function.class, ext -> ext.apply(model));
        Object result2 = ExtensionInvoker.invoke("SUB-SCOPE", "normal", Function.class, ext -> "Hello: " + ext.apply(model));
        Preconditions.checkState(StringUtils.startsWith(String.valueOf(result2), "Hello"));
        Assert.assertNotEquals(result1, result2);
    }

    @Test
    public void test_result_process2() {
        List<Object> result1 = ExtensionInvoker.invoke("normal", Function.class, ext -> ext.apply(model), Reducers.collect());
        List<Object> result2 = ExtensionInvoker.invoke("normal", Function.class, ext -> "Hello: " + ext.apply(model), Reducers.collect());
        Assert.assertNotEquals(result1, result2);
        Assert.assertTrue(result1.stream().allMatch(res -> res instanceof Map));
        Assert.assertTrue(result2.stream().allMatch(res -> StringUtils.startsWith(String.valueOf(res), "Hello")));
    }

}
