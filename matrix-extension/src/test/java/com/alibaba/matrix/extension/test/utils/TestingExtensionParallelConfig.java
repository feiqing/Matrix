package com.alibaba.matrix.extension.test.utils;

import com.alibaba.matrix.base.util.T;
import com.alibaba.matrix.extension.config.ExtensionParallelConfig;
import com.alibaba.matrix.extension.config.DefaultExtensionParallelConfig;
import com.alibaba.matrix.extension.model.ExtensionExecuteContext;
import com.google.auto.service.AutoService;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.function.TriFunction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/18 22:24.
 */
@AutoService(ExtensionParallelConfig.class)
public class TestingExtensionParallelConfig extends DefaultExtensionParallelConfig {

    private static final ExecutorService executor = Executors.newFixedThreadPool(2, new BasicThreadFactory.Builder().namingPattern("E-Parallel-Testing-Exec-%s").build());

    @Override
    public ExecutorService executor(ExtensionExecuteContext ctx) {
        if (ctx.extension == TriFunction.class) {
            return executor;
        }

        return super.executor(ctx);
    }

    @Override
    public long timeout(ExtensionExecuteContext ctx) {
        if (ctx.extension == TriFunction.class) {
            return T.OneS;
        }
        return super.timeout(ctx);
    }
}
