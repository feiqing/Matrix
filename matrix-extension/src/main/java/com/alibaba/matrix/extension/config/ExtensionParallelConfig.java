package com.alibaba.matrix.extension.config;

import com.alibaba.matrix.extension.model.ExtensionExecuteContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public interface ExtensionParallelConfig {

    default boolean enable(ExtensionExecuteContext ctx) {
        return true;
    }

    ExecutorService executor(ExtensionExecuteContext ctx);

    default boolean enableFailFast(ExtensionExecuteContext ctx) {
        return true;
    }

    long timeout(ExtensionExecuteContext ctx);

    TimeUnit unit(ExtensionExecuteContext ctx);
}
