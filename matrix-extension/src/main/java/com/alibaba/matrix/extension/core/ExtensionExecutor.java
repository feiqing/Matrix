package com.alibaba.matrix.extension.core;

import com.alibaba.matrix.base.telemetry.trace.ISpan;
import com.alibaba.matrix.extension.exception.ExtensionRuntimeException;
import com.alibaba.matrix.extension.model.ExtensionExecuteContext;
import com.alibaba.matrix.extension.model.ExtensionImplEntity;
import com.alibaba.matrix.extension.plugin.ExtensionInvocation;
import com.alibaba.matrix.extension.reducer.Reducer;
import com.alibaba.matrix.extension.util.Logger;
import com.alibaba.matrix.extension.util.Message;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.alibaba.matrix.base.telemetry.TelemetryProvider.metrics;
import static com.alibaba.matrix.base.telemetry.TelemetryProvider.tracer;
import static com.alibaba.matrix.extension.config.ExtensionConfigProvider.experiment;
import static com.alibaba.matrix.extension.config.ExtensionConfigProvider.parallel;
import static com.alibaba.matrix.extension.core.ExtensionManager.plugins;
import static com.alibaba.matrix.extension.core.ExtensionManager.router;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 2.0
 * @since 2022/8/11 17:47.
 */
@SuppressWarnings({"unchecked"})
public class ExtensionExecutor {

    public static <Ext, T, R> R execute(String scope, List<String> codes, Class<Ext> extension, Function<Ext, T> action, Reducer<T, R> reducer) {
        ExtensionExecuteContext ctx = new ExtensionExecuteContext(scope, codes, extension, action, reducer);
        List<ExtensionImplEntity> impls = router.route(ctx);
        if (CollectionUtils.isEmpty(impls)) {
            throw new ExtensionRuntimeException(Message.format("MATRIX-EXTENSION-0000-0002", extension.getName()));
        }

        if (experiment.enableJobExecutor(ctx)) {
            return (R) ExtensionJobExecutor.execute(ctx, impls);
        }

        if (impls.size() > 1 && parallel.enable(ctx) && reducer.parallel()) {
            return (R) parallelExecute(ctx, impls);
        } else {
            return (R) serialExecute(ctx, impls);
        }
    }

    private static Object parallelExecute(ExtensionExecuteContext ctx, List<ExtensionImplEntity> impls) {
        Executor executor = parallel.executor(ctx);
        Preconditions.checkState(executor != null);

        AtomicBoolean isBreak = new AtomicBoolean(false);
        List<ListenableFuture<Triple<Boolean, Object, Throwable>>> futures = new ArrayList<>(impls.size());
        for (ExtensionImplEntity impl : impls) {
            futures.add(Futures.submit(() -> {
                MutableTriple<Boolean, Object, Throwable> triple = new MutableTriple<>();
                if (isBreak.get()) {
                    triple.setLeft(false);
                    return triple;
                }

                triple.setLeft(true);
                try {
                    triple.setMiddle(executeImpl(ctx, impl));
                    // tips
                    if (ctx.reducer.willBreak(triple.getMiddle())) {
                        isBreak.set(true);
                    }
                } catch (Throwable t) {
                    triple.setRight(t);
                    isBreak.set(true);
                }

                return triple;
            }, executor));
        }

        ListenableFuture<List<Triple<Boolean, Object, Throwable>>> future = Futures.allAsList(futures);
        try {
            List<Triple<Boolean, Object, Throwable>> triples = future.get(parallel.timeout(ctx), parallel.unit(ctx));
            if (future.isDone()) {
                Throwable[] exceptions = triples.stream().map(Triple::getRight).filter(Objects::nonNull).toArray(Throwable[]::new);
                if (ArrayUtils.isNotEmpty(exceptions)) {
                    throw new ExtensionRuntimeException(Message.format("MATRIX-EXTENSION-0000-0006", ctx.extension.getName(), ctx.scope, StringUtils.join(ctx.codes, ','), exceptions.length), exceptions);
                }
                List<Object> results = triples.stream().filter(Triple::getLeft).map(Triple::getMiddle).collect(Collectors.toList());
                return ctx.reducer.reduce(results);
            } else {
                throw new ExtensionRuntimeException(Message.format("MATRIX-EXTENSION-0000-0007", ctx.extension.getName(), ctx.scope, StringUtils.join(ctx.codes, ',')));
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return ExceptionUtils.rethrow(e);
        }
    }

    private static Object serialExecute(ExtensionExecuteContext ctx, List<ExtensionImplEntity> impls) {
        List<Object> results = new ArrayList<>(impls.size());
        for (ExtensionImplEntity impl : impls) {
            Object result = executeImpl(ctx, impl);
            results.add(result);
            if (ctx.reducer.willBreak(result)) {
                break;
            }
        }
        return ctx.reducer.reduce(results);
    }

    private static Object executeImpl(ExtensionExecuteContext ctx, ExtensionImplEntity impl) {
        Preconditions.checkState(ctx.extension.isInstance(impl.instance));
        metrics.incCounter("execute_extension", Logger.formatExec(ctx, impl));
        ISpan span = tracer.newSpan("[Ext]:" + ctx.extension.getSimpleName(), Logger.formatExec(ctx, impl));
        try {
            Object proceed = new ExtensionInvocation(ctx, impl, plugins).proceed();
            span.setStatus(ISpan.STATUS_SUCCESS);
            return proceed;
        } catch (Throwable t) {
            span.event("ExecuteExtensionError", Logger.formatExec(ctx, impl));
            metrics.incCounter("execute_extension_error", Logger.formatExec(ctx, impl));
            span.setStatus(t);
            return ExceptionUtils.rethrow(t);
        } finally {
            span.finish();
        }
    }
}
