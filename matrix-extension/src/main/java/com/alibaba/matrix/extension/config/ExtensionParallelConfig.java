package com.alibaba.matrix.extension.config;

import com.alibaba.matrix.extension.model.ExtExecCtx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public interface ExtensionParallelConfig {

    boolean enable(ExtExecCtx ctx);

    ExecutorService executor(ExtExecCtx ctx);

    long timeout(ExtExecCtx ctx);

    TimeUnit unit(ExtExecCtx ctx);
}
