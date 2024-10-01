package com.alibaba.matrix.config.test;

import com.alibaba.matrix.base.util.T;
import com.alibaba.matrix.config.provider.ConfigService;
import com.alibaba.matrix.config.provider.NacosConfigService;
import com.google.auto.service.AutoService;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/10 23:01.
 */
@AutoService(ConfigService.class)
public class TestNacosConfigService implements ConfigService {

    private final NacosConfigService nacosConfigService = new NacosConfigService("localhost:8848", "", T.OneS);

    @Override
    public String getConfig(String namespace, String key) {
        return nacosConfigService.getConfig(namespace, key);
    }

    @Override
    public void addConfigListener(String namespace, String key, Consumer<String> consumer) {
        nacosConfigService.addConfigListener(namespace, key, consumer);
    }
}
