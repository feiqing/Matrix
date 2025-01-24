package com.alibaba.matrix.extension.config;

import com.alibaba.matrix.base.util.T;
import com.alibaba.matrix.extension.model.ExtensionExecuteContext;
import com.alibaba.ttl.threadpool.TtlExecutors;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
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
    public boolean enable(ExtensionExecuteContext ctx) {
        return true;
    }

    @Override
    public ExecutorService executor(ExtensionExecuteContext ctx) {
        return ExecutorHolder.executor;
    }

    @Override
    public long timeout(ExtensionExecuteContext ctx) {
        return T.OneS;
    }

    @Override
    public boolean enableFailFast(ExtensionExecuteContext ctx) {
        return true;
    }

    @Override
    public TimeUnit unit(ExtensionExecuteContext ctx) {
        return TimeUnit.MILLISECONDS;
    }
}
