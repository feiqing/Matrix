package com.alibaba.matrix.config.service;

import java.util.function.Consumer;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2017/4/23 22:51.
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
