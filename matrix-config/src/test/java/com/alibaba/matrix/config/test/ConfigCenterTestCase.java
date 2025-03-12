package com.alibaba.matrix.config.test;

import com.alibaba.matrix.base.util.T;
import com.alibaba.matrix.config.ConfigFrameworkRegister;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Collections;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/5/31 11:56.
 */
public class ConfigCenterTestCase {

    @Before
    public void setUp() {
        ConfigFrameworkRegister register = new ConfigFrameworkRegister(Collections.singletonList("com.alibaba.matrix.config.test"));
        register.init();
    }

    @Test
    public void test_config_reference() {
        System.out.println(ConfigCenterDemo.demoConfigValue);
    }


    @Test
    public void test() throws Throwable {
        for (Field field : ConfigCenterDemo.class.getFields()) {
            System.out.println(field.getName() + " = " + field.get(null));
        }

        Thread.sleep(T.OneM);
    }
}
