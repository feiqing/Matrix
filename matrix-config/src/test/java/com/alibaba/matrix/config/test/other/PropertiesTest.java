package com.alibaba.matrix.config.test.other;

import org.junit.Test;

import java.io.FileOutputStream;
import java.util.Properties;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2024/10/29 13:53.
 */
public class PropertiesTest {

    @Test
    public void test() throws Exception {
        Properties properties = new Properties();
        properties.load(this.getClass().getClassLoader().getResourceAsStream("matrix-config-test.properties"));

        properties.storeToXML(new FileOutputStream("matrix-config-test-2.xml"), "matrix-config-test");
        System.out.println(properties);


    }
}
