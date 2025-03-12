package com.alibaba.matrix.config.test.other;

import com.alibaba.matrix.config.service.provider.YamlConfigService;
import org.junit.Test;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2024/10/30 11:48.
 */
public class YamlTester {

    @Test
    public void test() {
        YamlConfigService yamlConfigService = new YamlConfigService();
        System.out.println(yamlConfigService.getConfig("xx", "xx"));
    }
}
