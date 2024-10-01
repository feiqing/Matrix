package com.alibaba.matrix.extension.config;

import com.alibaba.matrix.base.util.T;
import com.alibaba.matrix.extension.model.ExtExecCtx;
import com.alibaba.ttl.threadpool.TtlExecutors;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/8/28 11:05.
 */
public class DefaultExtensionParallelConfig implements ExtensionParallelConfig {

    private static final class ExecutorHolder {
        private static final ExecutorService executor = TtlExecutors.getTtlExecutorService(
                Executors.newFixedThreadPool(
                        Runtime.getRuntime().availableProcessors() * 2,
                        new BasicThreadFactory.Builder().namingPattern("E-Parallel-Common-Exec-%d").build()
                )
        );
    }

    @Override
    public boolean enable(ExtExecCtx ctx) {
        return true;
    }

    @Override
    public ExecutorService executor(ExtExecCtx ctx) {
        return ExecutorHolder.executor;
    }

    @Override
    public long timeout(ExtExecCtx ctx) {
        return T.OneS;
    }

    @Override
    public TimeUnit unit(ExtExecCtx ctx) {
        return TimeUnit.MILLISECONDS;
    }
}
