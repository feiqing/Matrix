package com.alibaba.matrix.config.provider;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2023/8/16 20:04.
 */
public interface ConfigService {

    /**
     * @param namespace
     * @param key
     * @return
     */
    String getConfig(String namespace, String key);

    /**
     * @param namespace
     * @param key
     * @param consumer: arg1=oldValue, arg2=newValue
     */
    void addConfigListener(String namespace, String key, Consumer<String> consumer);
}
