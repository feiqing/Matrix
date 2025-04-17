package com.alibaba.matrix.job;

import com.alibaba.matrix.base.telemetry.trace.ISpan;
import com.alibaba.matrix.job.util.Message;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.alibaba.matrix.base.telemetry.TelemetryProvider.metrics;
import static com.alibaba.matrix.base.telemetry.TelemetryProvider.tracer;

/**
 * The JobExecutor class is responsible for executing jobs with tasks that can be either serial or parallel.
 * It provides methods to execute jobs and gather results in different formats, such as a map, a list, or a detailed result object.
 * The executor also handles exceptions and logs relevant information using telemetry.
 *
 * @param <Input>  The type of the input parameter for the job
 * @param <Output> The type of the output value for the job tasks
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 2.0
 * @since 2020/9/18 13:41.
 */
@Slf4j(topic = "Job")
public class JobExecutor<Input, Output> {

    private final JobConfig config;

    /**
     * Constructs a JobExecutor with default configuration.
     */
    public JobExecutor() {
        this(new JobConfig(null, true));
    }

    /**
     * Constructs a JobExecutor with a specified ExecutorService and default fail-fast behavior.
     *
     * @param executor The ExecutorService to use for executing tasks
     */
    public JobExecutor(ExecutorService executor) {
        this(new JobConfig(executor, true));
    }

    /**
     * Constructs a JobExecutor with a specified ExecutorService and fail-fast behavior.
     *
     * @param executor       The ExecutorService to use for executing tasks
     * @param enableFailFast Whether to enable fail-fast behavior
     */
    public JobExecutor(ExecutorService executor, boolean enableFailFast) {
        this(new JobConfig(executor, enableFailFast));
    }

    /**
     * Constructs a JobExecutor with a specified JobConfig.
     *
     * @param config The JobConfig to use for configuring the executor
     */
    public JobExecutor(JobConfig config) {
        this.config = config;
    }

    /**
     * Executes the specified job without returning any result.
     * If an exception occurs during job execution, a JobWrappedMultipleFailureException will be thrown.
     *
     * @param job   The job to be executed
     * @param input The input parameter for the job
     * @throws JobWrappedMultipleFailureException If an exception occurs during job execution
     */
    public void execute(Job job, Input input) throws JobWrappedMultipleFailureException {
        doExecute(job, input, new JobContext(true, null));
    }

    /**
     * Executes the specified job and returns a List containing all task return values.
     * If an exception occurs during job execution, a JobWrappedMultipleFailureException will be thrown.
     *
     * @param job   The job to be executed
     * @param input The input parameter for the job
     * @return A List containing all task return values
     * @throws JobWrappedMultipleFailureException If an exception occurs during job execution
     */
    @SuppressWarnings("unchecked")
    public List<Output> executeForList(Job job, Input input) throws JobWrappedMultipleFailureException {
        JobContext context = doExecute(job, input, new JobContext(true, new ConcurrentLinkedQueue<>()));
        if (CollectionUtils.isEmpty(context.outputs)) {
            return Collections.emptyList();
        }
        return context.outputs.stream().map(pair -> (Output) pair.getRight()).collect(Collectors.toList());
    }

    /**
     * Executes the specified job and returns a Map where the keys are task keys and the values are task return values.
     * If an exception occurs during job execution, a JobWrappedMultipleFailureException will be thrown.
     *
     * @param job   The job to be executed
     * @param input The input parameter for the job
     * @return A Map where the keys are task keys and the values are task return values
     * @throws JobWrappedMultipleFailureException If an exception occurs during job execution, or if there are duplicate task keys
     */
    @SuppressWarnings("unchecked")
    public Map<String, List<Output>> executeForMap(Job job, Input input) throws JobWrappedMultipleFailureException {
        JobContext context = doExecute(job, input, new JobContext(true, new ConcurrentLinkedQueue<>()));
        if (CollectionUtils.isEmpty(context.outputs)) {
            return Collections.emptyMap();
        }
        Map<String, List<Output>> outputMap = new LinkedHashMap<>(context.outputs.size());
        for (Pair<String, Object> output : context.outputs) {
            outputMap.computeIfAbsent(output.getKey(), _K -> new ArrayList<>()).add((Output) output.getValue());
        }
        return outputMap;
    }

    /**
     * Executes the specified job and returns a JobResult object containing all task return values and exception information.
     *
     * @param job   The job to be executed
     * @param input The input parameter for the job
     * @return A JobResult object containing all task return values and exception information
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public JobResult<Output> executeForResult(Job job, Input input) {
        JobContext context = doExecute(job, input, new JobContext(false, new ConcurrentLinkedQueue<>()));
        JobResult<Output> result = new JobResult<>();
        result.setOutputs(Collections.unmodifiableCollection((Collection) context.outputs));
        result.setExceptions(Collections.unmodifiableCollection(context.exceptions));
        return result;
    }

    // ---- core helpers ---- //

    private JobContext doExecute(Job job, Input input, JobContext context) throws JobWrappedMultipleFailureException {
        Preconditions.checkArgument(job != null);
        Preconditions.checkArgument(config.executor != null || !hasParallelJob(job));
        if (CollectionUtils.isEmpty(job.tasks)) {
            return context;
        }

        this.executeJob(job, input, context);
        log.info("Job:[{}({})] Executed={}(Err:{}) Skipped={} Output={} Elapsed={}ms.", job.name, job.parallel ? "p" : "s", context.executed.get(), context.excepted.get(), context.skipped.get(), context.size(), context.cost());

        if (context.throwsExecuteException && CollectionUtils.isNotEmpty(context.exceptions)) {
            tracer.currentSpan().event("JobThrowExceptions", job.name);
            metrics.incCounter("job_throw_exceptions", job.name);
            throw new JobWrappedMultipleFailureException(Message.format("MATRIX-JOB-0000-0000", job.name), context.exceptions.toArray(new Throwable[0]));
        }

        return context;
    }

    private void executeJob(Job job, Input input, JobContext context) {
        metrics.incCounter("execute_job", job.name);
        ISpan span = tracer.newSpan("ExecuteJob", job.name);
        try {
            if (CollectionUtils.isEmpty(job.tasks)) {
                return;
            }
            if (!job.parallel /*|| CollectionUtils.size(job.tasks) <= 1*/) {
                executeSerialJob(job, input, context);
            } else {
                executeParallelJob(job, input, context);
            }
            span.setStatus(ISpan.STATUS_SUCCESS);
        } finally {
            span.finish();
        }
    }

    @SuppressWarnings("unchecked")
    private void executeSerialJob(Job job, Input input, JobContext context) {
        for (Object t : job.tasks) {
            if (t instanceof Task) {
                this.executeTask(job.name, (Task<Input, Output>) t, input, context);
            } else {
                this.executeJob((Job) t, input, context);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void executeParallelJob(Job job, Input input, JobContext context) {
        CountDownLatch latch = new CountDownLatch(job.tasks.size());
        for (Object t : job.tasks) {
            if (t instanceof Task) {
                this.submitTask(job.name, (Task<Input, Output>) t, input, latch, context);
            } else {
                this.submitJob((Job) t, input, latch, context);
            }
        }

        try {
            if (latch.await(job.timeout, job.unit)) {
                return;
            }

            context.addException(new TimeoutException(Message.format("MATRIX-JOB-0000-0001", job.name, job.timeout, job.unit.name())));

            tracer.currentSpan().event("JobAwaitTimeout", job.name);
            metrics.incCounter("job_await_timeout", job.name);
            log.error("Job:[{}] await timeout[{}/{}].", job.name, job.timeout, job.unit.name());
        } catch (Throwable t) {
            context.addException(t);

            tracer.currentSpan().event("JobAwaitError", job.name);
            metrics.incCounter("job_await_error", job.name);
            log.error("Job:[{}] await error.", job.name, t);
        }
    }

    private void submitJob(Job job, Input input, CountDownLatch latch, JobContext context) {
        try {
            metrics.incCounter("submit_job", job.name);
            config.executor.submit(() -> {
                try {
                    this.executeJob(job, input, context);
                } finally {
                    latch.countDown();
                }
            });
        } catch (Throwable t) {
            latch.countDown();
            context.addException(t);

            tracer.currentSpan().event("SubmitJobError", job.name);
            metrics.incCounter("submit_job_error", job.name);
            log.error("Submit job:[{}] error.", job.name, t);
        }
    }

    private void submitTask(String jobName, Task<Input, Output> task, Input input, CountDownLatch latch, JobContext context) {
        String taskName = String.format("%s:%s", jobName, task.name());
        try {
            metrics.incCounter("submit_task", taskName);
            config.executor.submit(() -> {
                try {
                    this.executeTask(jobName, task, input, context);
                } finally {
                    latch.countDown();
                }
            });
        } catch (Throwable t) {
            latch.countDown();
            context.addException(t);

            tracer.currentSpan().event("SubmitTaskError", taskName);
            metrics.incCounter("submit_task_error", taskName);
            log.error("Submit task:[{}] error.", taskName, t);
        }
    }

    private void executeTask(String jobName, Task<Input, Output> task, Input input, JobContext context) {
        String taskName = String.format("%s:%s", jobName, task.name());
        if (config.enableFailFast & !context.exceptions.isEmpty()) {
            context.skipped.incrementAndGet();

            tracer.currentSpan().event("ExecuteTaskSkipped", taskName);
            metrics.incCounter("execute_task_skipped", taskName);
            log.warn("Job task:[{}] is skipped.", taskName);
            return;
        }

        metrics.incCounter("execute_task", taskName);
        ISpan span = tracer.newSpan("ExecuteTask", taskName);
        Output output = null;
        try {
            String key = task.key(input);
            try {
                task.setUp(input);
                output = task.execute(input);
            } finally {
                task.tearDown(input, output);
            }
            context.addOutput(key, output);
            span.setStatus(ISpan.STATUS_SUCCESS);
        } catch (Throwable t) {
            span.setStatus(t);
            context.addException(t);
            context.excepted.incrementAndGet();

            span.event("ExecuteTaskError", taskName);
            metrics.incCounter("execute_task_error", taskName);
            log.error("Task:[{}] execute error.", taskName, t);
        } finally {
            context.executed.incrementAndGet();
            span.finish();
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
