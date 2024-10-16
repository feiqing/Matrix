package com.alibaba.matrix.extension.test;

import com.alibaba.matrix.extension.ExtensionInvoker;
import com.alibaba.matrix.extension.test.domain.TestModel;
import com.alibaba.matrix.extension.test.ext.DemoRemoteExt;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/8 22:10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-*.xml")
public class RemoteExtensionTestCase implements Serializable {

    private final TestModel model = new TestModel();

    {
        model.name = "feiqing";
        model.list = Collections.singletonList(18);
        model.map = Collections.singletonMap("key", "value-101791");
    }

    @Test
    public void test_remote_base() {
        Object invoke = ExtensionInvoker.invoke("code.base.none.impl", DemoRemoteExt.class, ext -> ext.apply(null, model, null));
        Assert.assertNotNull(invoke);
        Assert.assertTrue(String.valueOf(invoke).startsWith("Base: "));
    }

    @Test
    public void test_http_post() {
        Map<String, Object> map = (Map<String, Object>) ExtensionInvoker.invoke("code.http.post", DemoRemoteExt.class, ext -> ext.apply("arg1", model, null));
        Assert.assertEquals(map.get("name"), model.name);
    }

    @Test
    public void test_http_get() {
        List<Integer> result = (List<Integer>) ExtensionInvoker.invoke("code.http.get", DemoRemoteExt.class, ext -> ext.apply("arg1", model.list, null));
        Assert.assertTrue(CollectionUtils.isEqualCollection(model.list, result));
    }

    @Test
    public void test_dubbo() {
        String result = (String) ExtensionInvoker.invoke("code.dubbo", DemoRemoteExt.class, ext -> ext.apply("arg1", model, null));
        Assert.assertTrue(result.contains("Dubbo server"));
        Assert.assertTrue(result.contains(model.name));
    }
}
