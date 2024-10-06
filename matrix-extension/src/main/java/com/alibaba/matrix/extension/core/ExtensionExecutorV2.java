package com.alibaba.matrix.extension.core;

import com.alibaba.matrix.extension.exception.ExtensionRuntimeException;
import com.alibaba.matrix.extension.model.ExtExecCtx;
import com.alibaba.matrix.extension.model.ExtImpl;
import com.alibaba.matrix.extension.plugin.ExtensionInvocation;
import com.alibaba.matrix.extension.reducer.Reducer;
import com.alibaba.matrix.extension.util.Logger;
import com.alibaba.matrix.job.Job;
import com.alibaba.matrix.job.JobExecuteException;
import com.alibaba.matrix.job.JobExecutor;
import com.alibaba.matrix.job.Task;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.alibaba.matrix.extension.config.ExtensionConfigProvider.parallel;
import static com.alibaba.matrix.extension.core.ExtensionManager.plugins;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 2.0
 * @since 2022/8/11 17:47.
 */
public class ExtensionExecutorV2 {

    static Object executeByJob(ExtExecCtx ctx, List<ExtImpl> impls, Reducer reducer) {
        AtomicBoolean isBreak = new AtomicBoolean(false);
        Job.Builder<Void, Pair<Boolean, Object>> jobBuilder = Job.newBuilder();
        for (ExtImpl impl : impls) {
            jobBuilder.addTask(new Task<Void, Pair<Boolean, Object>>() {
                @Override
                public Pair<Boolean, Object> execute(Void o) {
                    if (isBreak.get()) {
                        return Pair.of(false, null);
                    }
                    try {
                        Object result = executeImpl(ctx, impl);
                        if (ctx.reducer.willBreak(result)) {
                            isBreak.set(true);
                        }
                        return Pair.of(true, result);
                    } catch (Throwable t) {
                        isBreak.set(true);
                        return ExceptionUtils.rethrow(t);
                    }
                }

                @Override
                public String name() {
                    return Logger.formatExec(ctx, impl);
                }
            });
        }

        Job job;
        if (impls.size() > 1 && parallel.enable(ctx) && reducer.parallel()) {
            job = jobBuilder.buildParallelJob("Extension", parallel.timeout(ctx), parallel.unit(ctx));
        } else {
            job = jobBuilder.buildSerialJob("Extension");
        }

        try {
            List<Object> results = new JobExecutor<Object, Pair<Boolean, Object>>(parallel.executor(ctx), true)
                    .executeForList(job, null)
                    .stream()
                    .filter(Pair::getKey)
                    .map(Pair::getValue)
                    .collect(Collectors.toList());

            return reducer.reduce(results);
        } catch (JobExecuteException e) {
            if (ArrayUtils.isNotEmpty(e.getCauses())) {
                String exceptions = Arrays.stream(e.getCauses()).map(ExceptionUtils::getMessage).collect(Collectors.joining(", ", "[", "]"));
                String message = String.format("Extension:[%s] scope:[%s] code:[%s] execute throw [%s] exception(s):%s, detail please check causes.", ctx.ext.getName(), ctx.scope, ctx.code, e.getCauses().length, exceptions);
                throw new ExtensionRuntimeException(message, e.getCauses());
            }
            throw e;
        }
    }

    private static Object executeImpl(ExtExecCtx ctx, ExtImpl impl) {
        Preconditions.checkState(ctx.ext.isInstance(impl.instance));
        return new ExtensionInvocation(ctx, impl, plugins).proceed();
    }
}
