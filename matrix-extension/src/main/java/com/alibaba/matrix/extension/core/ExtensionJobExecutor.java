package com.alibaba.matrix.extension.core;

import com.alibaba.matrix.extension.exception.ExtensionRuntimeException;
import com.alibaba.matrix.extension.model.ExtensionExecuteContext;
import com.alibaba.matrix.extension.model.ExtensionImplEntity;
import com.alibaba.matrix.extension.plugin.ExtensionInvocation;
import com.alibaba.matrix.extension.util.Logger;
import com.alibaba.matrix.extension.util.Message;
import com.alibaba.matrix.job.Job;
import com.alibaba.matrix.job.JobExecutor;
import com.alibaba.matrix.job.JobResult;
import com.alibaba.matrix.job.Task;
import com.google.common.base.Preconditions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.alibaba.matrix.extension.config.ExtensionConfigProvider.parallel;
import static com.alibaba.matrix.extension.core.ExtensionManager.plugins;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 2.0
 * @since 2022/8/11 17:47.
 */
@SuppressWarnings({"unchecked"})
class ExtensionJobExecutor {

    static Object execute(ExtensionExecuteContext ctx, List<ExtensionImplEntity> impls) {
        Job.Builder<AtomicBoolean, Object[]> builder = Job.newBuilder();
        for (ExtensionImplEntity impl : impls) {
            builder.addTask(new Task<AtomicBoolean, Object[]>() {
                @Override
                public Object[] execute(AtomicBoolean _break) {
                    if (_break.get()) {
                        return null;
                    }
                    try {
                        Object result = executeImpl(ctx, impl);
                        if (ctx.reducer.willBreak(result)) {
                            _break.set(true);
                        }
                        return new Object[]{result};
                    } catch (Throwable t) {
                        _break.set(true);
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
        if (impls.size() > 1 && parallel.enable(ctx) && ctx.reducer.parallel()) {
            job = builder.buildParallelJob("[Ext]:" + ctx.extension.getSimpleName(), parallel.timeout(ctx), parallel.unit(ctx));
        } else {
            job = builder.buildSerialJob("[Ext]:" + ctx.extension.getSimpleName());
        }

        JobResult<Object[]> result = new JobExecutor<AtomicBoolean, Object[]>(parallel.executor(ctx), parallel.enableFailFast(ctx)).executeForResult(job, new AtomicBoolean(false));
        if (CollectionUtils.isNotEmpty(result.getExceptions())) {
            throw new ExtensionRuntimeException(Message.format("MATRIX-EXTENSION-0000-0006", ctx.extension.getName(), ctx.scope, StringUtils.join(ctx.codes, ','), result.getExceptions().size()), result.getExceptions().toArray(new Throwable[0]));
        }

        List<Object> results = result
                .getOutputs()
                .stream()
                .map(Pair::getValue)
                .filter(Objects::nonNull)
                .map(array -> array[0])
                .collect(Collectors.toList());

        return ctx.reducer.reduce(results);
    }

    private static Object executeImpl(ExtensionExecuteContext ctx, ExtensionImplEntity impl) {
        Preconditions.checkState(ctx.extension.isInstance(impl.instance));
        return new ExtensionInvocation(ctx, impl, plugins).proceed();
    }
}
