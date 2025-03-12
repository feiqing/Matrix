package com.alibaba.matrix.config.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2025/3/26 13:53.
 */
public interface ConfigFrameworkParallelConfig {

    default boolean enable() {
        return true;
    }

    ExecutorService executor();

    long timeout();

    TimeUnit unit();

}
