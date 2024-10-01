package com.alibaba.matrix.config.provider;

import java.util.function.Consumer;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/8/28 11:26.
 */
public class NoopConfigService implements ConfigService {

    @Override
    public String getConfig(String namespace, String key) {
        return null;
    }

    @Override
    public void addConfigListener(String namespace, String key, Consumer<String> consumer) {
    }
}
