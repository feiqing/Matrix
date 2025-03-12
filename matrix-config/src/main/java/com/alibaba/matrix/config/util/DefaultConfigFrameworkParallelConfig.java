package com.alibaba.matrix.config.util;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2025/3/26 13:55.
 */
public class DefaultConfigFrameworkParallelConfig implements ConfigFrameworkParallelConfig {

    private static final class ExecutorHolder {
        private static final ExecutorService executor = Executors.newCachedThreadPool(new BasicThreadFactory.Builder().namingPattern("C-Parallel-Common-Exec-%d").build());
    }

    @Override
    public ExecutorService executor() {
        return ExecutorHolder.executor;
    }

    @Override
    public long timeout() {
        return 10;
    }

    @Override
    public TimeUnit unit() {
        return TimeUnit.SECONDS;
    }
}
