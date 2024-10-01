package com.alibaba.matrix.extension.config;

import com.alibaba.matrix.extension.model.ExtExecCtx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/8/27 22:07.
 */
public interface ExtensionParallelConfig {

    boolean enable(ExtExecCtx ctx);

    ExecutorService executor(ExtExecCtx ctx);

    long timeout(ExtExecCtx ctx);

    TimeUnit unit(ExtExecCtx ctx);
}
