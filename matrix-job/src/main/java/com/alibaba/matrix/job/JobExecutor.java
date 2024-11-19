package com.alibaba.matrix.job;

import com.alibaba.matrix.base.telemetry.trace.ISpan;
import com.alibaba.matrix.job.util.Message;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.alibaba.matrix.base.telemetry.TelemetryProvider.metrics;
import static com.alibaba.matrix.base.telemetry.TelemetryProvider.tracer;

/**
 * @author feiqing.zjf@gmail.com
 * @version 2.0
 * @since 2020/9/18 13:41.
 */
@Slf4j(topic = "Job")
public class JobExecutor<Input, Output> {

    private final ExecutorService executor;

    private final boolean enableFailFast;

    public JobExecutor() {
        this(null);
    }

    public JobExecutor(ExecutorService executor) {
        this(executor, true);
    }

    public JobExecutor(ExecutorService executor, boolean enableFailFast) {
        this.executor = executor;
        this.enableFailFast = enableFailFast;
    }

    /**
     * 不需要获取返回值的execute
     *
     * @param job
     * @param input
     */
    public void execute(Job job, Input input) throws JobExecuteException {
        _execute(job, input, new Wrapper());
    }

    /**
     * 要保证该批job内的task_key唯一, 否则会抛出JobExecuteException异常
     *
     * @param job
     * @param input
     * @return Map<key = task_key, value = task_return_value>
     */
    public Map<String, Output> executeForMap(Job job, Input input) throws JobExecuteException {
        Collection<Pair<String, Output>> outputs = _execute(job, input, new Wrapper(new ConcurrentLinkedQueue<>()));
        if (CollectionUtils.isEmpty(outputs)) {
            return Collections.emptyMap();
        }

        Map<String, Output> outputMap = new LinkedHashMap<>(outputs.size());
        for (Pair<String, Output> output : outputs) {
            if (outputMap.put(output.getKey(), output.getValue()) != null) {
                tracer.currentSpan().event("TaskKeyDuplicated", job.name);
                metrics.incCounter("task_key_duplicated", job.name);
                log.error("Job:[{}] task key duplicated.", job.name);
                throw new JobExecuteException(Message.format("MATRIX-JOB-0000-0001", job.name), null);
            }
        }
        return outputMap;
    }

    /**
     * @param job
     * @param input
     * @return List<value = task_return_value>
     */
    public List<Output> executeForList(Job job, Input input) throws JobExecuteException {
        Collection<Pair<String, Output>> outputs = _execute(job, input, new Wrapper(new ConcurrentLinkedQueue<>()));
        if (CollectionUtils.isEmpty(outputs)) {
            return Collections.emptyList();
        }
        return outputs.stream().map(Pair::getValue).collect(Collectors.toList());
    }

    private Collection<Pair<String, Output>> _execute(Job job, Input input, Wrapper wrapper) throws JobExecuteException {
        Preconditions.checkArgument(job != null);
        Preconditions.checkArgument(executor != null || !hasParallelJob(job));
        if (CollectionUtils.isEmpty(job.tasks)) {
            return Collections.emptyList();
        }

        this.handleJob(job, input, wrapper);
        log.info("Summary:[{}] executed:[{}](except={}) skipped:[{}] outputs:[{}] cost:[{}]ms.", job.name, wrapper.executed.get(), wrapper.excepted.get(), wrapper.skipped.get(), wrapper.size(), wrapper.cost());

        if (CollectionUtils.isNotEmpty(wrapper.exceptions)) {
            tracer.currentSpan().event("JobThrowExceptions", job.name);
            metrics.incCounter("job_throw_exceptions", job.name);
            throw new JobExecuteException(Message.format("MATRIX-JOB-0000-0000", job.name), wrapper.exceptions.toArray(new Throwable[0]));
        }

        return wrapper.outputs;
    }

    private void handleJob(Job job, Input input, Wrapper wrapper) {
        if (CollectionUtils.isEmpty(job.tasks)) {
            return;
        }

        if (!job.parallel /*|| CollectionUtils.size(job.tasks) <= 1*/) {
            handleSerialJob(job, input, wrapper);
        } else {
            handleParallelJob(job, input, wrapper);
        }
    }

    @SuppressWarnings("unchecked")
    private void handleSerialJob(Job job, Input input, Wrapper wrapper) {
        for (Object t : job.tasks) {
            if (t instanceof Task) {
                this.doExecuteTask(job.name, (Task<Input, Output>) t, input, wrapper);
            } else {
                this.handleJob((Job) t, input, wrapper);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void handleParallelJob(Job job, Input input, Wrapper wrapper) {
        CountDownLatch latch = new CountDownLatch(job.tasks.size());
        for (Object t : job.tasks) {
            if (t instanceof Task) {
                this.doSubmitTask(job.name, (Task<Input, Output>) t, input, latch, wrapper);
            } else {
                this.doSubmitJob((Job) t, input, latch, wrapper);
            }
        }

        try {
            if (latch.await(job.timeout, job.unit)) {
                return;
            }

            wrapper.addException(new TimeoutException(Message.format("MATRIX-JOB-0000-0002", job.name)));

            tracer.currentSpan().event("JobAwaitTimeout", job.name);
            metrics.incCounter("job_await_timeout", job.name);
            log.error("Job:[{}] await timeout.", job.name);
        } catch (Throwable t) {
            wrapper.addException(t);

            tracer.currentSpan().event("JobAwaitError", job.name);
            metrics.incCounter("job_await_error", job.name);
            log.error("Job:[{}] await error.", job.name, t);
        }
    }

    private void doSubmitJob(Job job, Input input, CountDownLatch latch, Wrapper wrapper) {
        try {
            metrics.incCounter("submit_executor_job", job.name);
            executor.submit(() -> {
                try {
                    this.handleJob(job, input, wrapper);
                } finally {
                    latch.countDown();
                }
            });
        } catch (Throwable t) {
            latch.countDown();
            wrapper.addException(t);

            tracer.currentSpan().event("SubmitJobError", job.name);
            metrics.incCounter("submit_job_error", job.name);
            log.error("Submit job:[{}] error.", job.name, t);
        }
    }

    private void doSubmitTask(String jobName, Task<Input, Output> task, Input input, CountDownLatch latch, Wrapper wrapper) {
        String taskName = String.format("%s:%s", jobName, task.name());
        try {
            metrics.incCounter("submit_executor_task", taskName);
            executor.submit(() -> {
                try {
                    this.doExecuteTask(jobName, task, input, wrapper);
                } finally {
                    latch.countDown();
                }
            });
        } catch (Throwable t) {
            latch.countDown();
            wrapper.addException(t);

            tracer.currentSpan().event("SubmitTaskError", taskName);
            metrics.incCounter("submit_task_error", taskName);
            log.error("Submit task:[{}] error.", taskName, t);
        }
    }

    private void doExecuteTask(String jobName, Task<Input, Output> task, Input input, Wrapper wrapper) {
        String taskName = String.format("%s:%s", jobName, task.name());
        if (enableFailFast & !wrapper.exceptions.isEmpty()) {
            wrapper.skipped.incrementAndGet();

            tracer.currentSpan().event("ExecuteTaskSkipped", taskName);
            metrics.incCounter("execute_task_skipped", taskName);
            log.info("Job task:[{}] is skipped.", taskName);
            return;
        }

        metrics.incCounter("execute_job_task", taskName);
        ISpan span = tracer.newSpan("JobTask", taskName);
        Output output = null;
        try {
            String key = task.key(input);
            try {
                task.setUp(input);
                output = task.execute(input);
            } finally {
                task.tearDown(input, output);
            }
            wrapper.addOutput(key, output);
            span.setStatus(ISpan.STATUS_SUCCESS);
        } catch (Throwable t) {
            span.setStatus(t);
            wrapper.addException(t);
            wrapper.excepted.incrementAndGet();

            span.event("ExecuteJobTaskError", taskName);
            metrics.incCounter("execute_job_task_error", taskName);
            log.error("Job task:[{}] execute error.", taskName, t);
        } finally {
            wrapper.executed.incrementAndGet();
            span.finish();
        }
    }

    private class Wrapper implements Serializable {

        private static final long serialVersionUID = 6392898597976639655L;

        private final Collection<Pair<String, Output>> outputs;

        private final Collection<Throwable> exceptions = new ConcurrentLinkedQueue<>();

        private final AtomicLong executed = new AtomicLong(0L);

        private final AtomicLong excepted = new AtomicLong(0L);

        private final AtomicLong skipped = new AtomicLong(0L);

        private final long start = System.currentTimeMillis();

        public Wrapper() {
            this(null);
        }

        public Wrapper(Collection<Pair<String, Output>> outputs) {
            this.outputs = outputs;
        }

        public int size() {
            return CollectionUtils.size(outputs);
        }

        public long cost() {
            return System.currentTimeMillis() - start;
        }

        public void addOutput(String key, Output output) {
            if (outputs != null) {
                outputs.add(Pair.of(key, output));
            }
        }

        public void addException(Throwable throwable) {
            if (throwable != null) {
                exceptions.add(throwable);
            }
        }
    }

    private static boolean hasParallelJob(Job job) {
        if (job == null) {
            return false;
        }
        if (job.parallel) {
            return true;
        }
        for (Object t : job.tasks) {
            if (t instanceof Job) {
                if (hasParallelJob((Job) t)) {
                    return true;
                }
            }
        }
        return false;
    }
}
