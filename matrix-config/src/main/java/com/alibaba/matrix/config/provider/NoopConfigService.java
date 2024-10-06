package com.alibaba.matrix.config.provider;

import java.util.function.Consumer;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2017/4/23 22:51.
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
