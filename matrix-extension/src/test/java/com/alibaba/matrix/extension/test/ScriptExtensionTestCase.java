package com.alibaba.matrix.extension.test;

import com.alibaba.matrix.extension.ExtensionInvoker;
import com.alibaba.matrix.extension.test.domain.TestModel;
import com.alibaba.matrix.extension.test.ext.DemoScriptExt;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
public class ScriptExtensionTestCase {

    private final TestModel model = new TestModel();

    {
        model.name = "feiqing";
        model.list = Collections.singletonList(18);
        model.map = Collections.singletonMap("key", "value-101791");
    }

    @Test
    public void test_script_base() {
        Object invoke = ExtensionInvoker.invoke("code.base.none.impl", DemoScriptExt.class, ext -> ext.apply(null, null, null));
        Assert.assertNull(invoke);
    }

    @Test
    public void test_script_spel_nacos() throws InterruptedException {
        for (int i = 0; i < 100; ++i) {
            Object invoke = ExtensionInvoker.invoke("code.spel.nacos", DemoScriptExt.class, ext -> ext.apply("arg1", model, null));
            MatcherAssert.assertThat(invoke, new BaseMatcher<Object>() {

                @Override
                public boolean matches(Object item) {
                    if (item instanceof String) {
                        return StringUtils.equalsIgnoreCase((String) item, model.name);
                    }
                    if (item instanceof List) {
                        return model.list.equals(item);
                    }
                    if (item instanceof Map) {
                        return model.map.equals(item);
                    }

                    return false;
                }

                @Override
                public void describeTo(Description description) {

                }
            });
            Thread.sleep(3000);
        }
    }

    @Test
    public void test_remote_groovy_file() {
        Object invoke = ExtensionInvoker.invoke("code.groovy.file", DemoScriptExt.class, ext -> ext.apply("arg1", model, null));
        Assert.assertEquals("SUCCESS", invoke);
    }

    @Test
    public void test_remote_groovy_nacos() throws InterruptedException {
        for (int i = 0; i < 100; ++i) {
            Object invoke = ExtensionInvoker.invoke("code.groovy.nacos", DemoScriptExt.class, ext -> ext.apply("arg1", model, null));
            Assert.assertEquals("SUCCESS", invoke);
            Thread.sleep(3000);
        }
    }

    @Test
    public void test_remote_groovy_http() {
        Map<String, Object> map = (Map<String, Object>) ExtensionInvoker.invoke("code.groovy.http", DemoScriptExt.class, ext -> ext.apply("arg1", model, null));
        Assert.assertEquals(map.get("result"), model.name);
    }

}
