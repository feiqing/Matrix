package com.alibaba.matrix.job;

import com.alibaba.matrix.base.message.Message;
import com.alibaba.matrix.base.telemetry.trace.ISpan;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.alibaba.matrix.base.telemetry.TelemetryProvider.tracer;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/8/7 22:43.
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
        doExecute(job, input, new Wrapper());
    }

    /**
     * 要保证该批job内的task_key唯一, 否则会抛出JobExecuteException异常
     *
     * @param job
     * @param input
     * @return Map<key = task_key, value = task_return_value>
     */
    public Map<String, Output> executeForMap(Job job, Input input) throws JobExecuteException {
        Collection<Pair<String, Output>> outputs = doExecute(job, input, new Wrapper(new ConcurrentLinkedQueue<>()));

        if (CollectionUtils.isEmpty(outputs)) {
            return Collections.emptyMap();
        }

        Map<String, Output> outputMap = new LinkedHashMap<>(outputs.size());
        for (Pair<String, Output> output : outputs) {
            if (outputMap.put(output.getKey(), output.getValue()) == null) {
                continue;
            }

            Monitor.monitorAlarm("TASK_KEY_DUPLICATED:" + job.name);
            String message = Message.of("MATRIX-JOB-0000-0002", job.name, output.getKey()).getMessage();
            log.error("{}", message);
            throw new JobExecuteException(message);
        }
        return outputMap;
    }

    /**
     * @param job
     * @param input
     * @return List<value = task_return_value>
     */
    public List<Output> executeForList(Job job, Input input) throws JobExecuteException {
        Collection<Pair<String, Output>> outputs = doExecute(job, input, new Wrapper(new ConcurrentLinkedQueue<>()));
        if (CollectionUtils.isEmpty(outputs)) {
            return Collections.emptyList();
        }
        return outputs.stream().map(Pair::getValue).collect(Collectors.toList());
    }

    /**
     * Job执行的统一入口
     */
    private Collection<Pair<String, Output>> doExecute(Job job, Input input, Wrapper wrapper) throws JobExecuteException {
        Preconditions.checkArgument(job != null);
        Preconditions.checkArgument(executor != null || !hasParallelJob(job));

        if (CollectionUtils.isEmpty(job.tasks)) {
            log.warn("Job:[{}] tasks is empty.", job.name);
            return Collections.emptyList();
        }

        this.handleJob(job, input, wrapper);
        log.info("Job summary: [{}] executed:[{}] skipped:[{}] excepted:[{}] outputs:[{}] cost:[{}]ms.", job.name, wrapper.executed.get(), wrapper.skipped.get(), wrapper.excepted.get(), wrapper.size(), wrapper.cost());

        if (CollectionUtils.isNotEmpty(wrapper.exceptions)) {
            Monitor.monitorAlarm("JOB_THROW_EXCEPTION(S):" + job.name);
            throw new JobExecuteException(Message.of("MATRIX-JOB-0000-0000", job.name).getMessage(), wrapper.exceptions.toArray(new Throwable[0]));
        }

        if (wrapper.hasFailFast()) {
            Monitor.monitorAlarm("JOB_FAIL_FAST:" + job.name);
            throw new JobExecuteException(Message.of("MATRIX-JOB-0000-0001", job.name, wrapper.failFast.get()).getMessage());
        }

        return wrapper.outputs;
    }

    private void handleJob(Job job, Input input, Wrapper wrapper) {
        long start = System.currentTimeMillis();
        try {
            if (!job.parallel /*|| CollectionUtils.size(job.tasks) <= 1*/) {
                handleSerialJob(job, input, wrapper);
            } else {
                handleParallelJob(job, input, wrapper);
            }
        } finally {
            log.info("Job:[{}] execute cost:[{}]ms.", job.name, System.currentTimeMillis() - start);
        }
    }

    private void handleSerialJob(Job job, Input input, Wrapper wrapper) {
        if (CollectionUtils.isEmpty(job.tasks)) {
            return;
        }

        for (Object t : job.tasks) {
            if (t instanceof Task) {
                doExecuteTask(job.name, (Task<Input, Output>) t, input, wrapper);
            } else {
                /*递归调用*/
                this.handleJob((Job) t, input, wrapper);
            }
        }
    }

    private void handleParallelJob(Job job, Input input, Wrapper wrapper) {
        if (CollectionUtils.isEmpty(job.tasks)) {
            return;
        }

        CountDownLatch latch = new CountDownLatch(job.tasks.size());
        for (Object t : job.tasks) {
            if (t instanceof Task) {
                Task<Input, Output> subTask = (Task<Input, Output>) t;
                doSubmitRunnable("Task", subTask.name(), () -> doExecuteTask(job.name, subTask, input, wrapper), latch, wrapper);
            } else {
                Job subJob = (Job) t;
                doSubmitRunnable("Job", subJob.name, () -> /*递归调用*/this.handleJob(subJob, input, wrapper), latch, wrapper);
            }
        }

        try {

            if (!latch.await(job.timeout, job.unit)) {
                String message = String.format("Job:[%s] await timeout:[%s/%s].", job.name, job.timeout, job.unit);

                wrapper.makeFailFast(String.format("JobAwaitTimeout:%s", job.name));
                wrapper.addException(new TimeoutException(message));

                Monitor.monitorAlarm("JOB_AWAIT_TIMEOUT:" + job.name);
                log.error("{}", message);
            }
        } catch (Throwable t) {

            wrapper.makeFailFast(String.format("JobAwaitError:%s", job.name));
            wrapper.addException(t);

            Monitor.monitorAlarm("JOB_AWAIT_ERROR:" + job.name);
            log.error("Job:[{}] await error.", job.name, t);
        }
    }

    private void doSubmitRunnable(String type, String name, Runnable runnable, CountDownLatch latch, Wrapper wrapper) {
        if (wrapper.hasFailFast()) {
            log.info("{}:[{}] is skipped by fail fast.", type, name);
            latch.countDown();
            wrapper.skipped.incrementAndGet();
            return;
        }

        try {
            Monitor.monitorBusiness(String.format("SUBMIT_%s:%s", type.toUpperCase(), name));
            executor.submit(() -> {
                try {
                    runnable.run();
                } finally {
                    latch.countDown();
                }
            });
        } catch (Throwable t) {
            latch.countDown();

            wrapper.makeFailFast(String.format("Submit%sError:%s", type, name));
            wrapper.addException(t);

            Monitor.monitorAlarm(String.format("SUBMIT_%s_ERROR:%s", type.toUpperCase(), name));
            log.error("Submit {}:[{}] error.", type, name, t);
        }
    }

    private void doExecuteTask(String jobName, Task<Input, Output> task, Input input, Wrapper wrapper) {
        String taskName = task.name();
        if (wrapper.hasFailFast()) {
            log.info("Job:[{}] task:[{}] is skipped by fail fast.", jobName, taskName);
            wrapper.skipped.incrementAndGet();
            return;
        }

        Monitor.monitorBusiness("EXECUTE_TASK:" + taskName);

        String key;
        try {
            key = task.key(input);
        } catch (Throwable t) {
            key = null;
            wrapper.addException(t);
            Monitor.monitorAlarm("GENERATE_KEY_ERROR:" + taskName);
            log.error("Job:[{}] task:[{}] generate key error.", jobName, taskName, t);
        }

        ISpan span = tracer.newSpan("ExecuteJobTask", taskName);
        Output output = null;
        Throwable throwable = null;
        try {
            try {
                task.setUp(input);
                output = task.execute(input);
            } finally {
                task.tearDown(input, output);
            }
            span.setStatus(ISpan.STATUS_SUCCESS);
        } catch (Throwable t) {
            wrapper.makeFailFast(String.format("TaskExecuteError:%s", taskName));

            throwable = t;
            span.setStatus(t);
            wrapper.excepted.incrementAndGet();

            Monitor.monitorAlarm("TASK_EXECUTE_ERROR:" + taskName);
            log.error("Job:[{}] task:[{}] execute error.", jobName, taskName, t);
        } finally {
            wrapper.addOutput(key, output, throwable);
            wrapper.executed.incrementAndGet();
            span.finish();
        }
    }


    private class Wrapper implements Serializable {

        private static final long serialVersionUID = 6392898597976639655L;

        private final Collection<Throwable> exceptions = new ConcurrentLinkedQueue<>();

        private final Collection<Pair<String, Output>> outputs;

        private final AtomicLong executed = new AtomicLong(0L);

        private final AtomicLong skipped = new AtomicLong(0L);

        private final AtomicLong excepted = new AtomicLong(0L);

        private final AtomicReference<String> failFast = new AtomicReference<>();

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

        public void addOutput(String key, Output output, Throwable throwable) {
            if (throwable != null) {
                exceptions.add(throwable);
            }
            if (outputs != null) {
                outputs.add(Pair.of(key, output));
            }
        }

        public void addException(Throwable throwable) {
            if (throwable != null) {
                exceptions.add(throwable);
            }
        }

        public void makeFailFast(String cause) {
            if (failFast.compareAndSet(null, cause)) {
                Monitor.monitorAlarm(String.format("FAIL_FAST:%s", cause));
                log.warn("Fail fast, caused by [{}].", cause);
            }
        }

        public boolean hasFailFast() {
            return enableFailFast && failFast.get() != null;
        }
    }

    private boolean hasParallelJob(Job job) {
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
