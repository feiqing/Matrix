package com.alibaba.matrix.job;

import com.alibaba.matrix.base.telemetry.trace.ISpan;
import com.alibaba.matrix.job.util.Message;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.alibaba.matrix.base.telemetry.TelemetryProvider.metrics;
import static com.alibaba.matrix.base.telemetry.TelemetryProvider.tracer;

/**
 * The JobExecutor class is responsible for executing jobs, which are composed of a series of tasks.
 * It supports both serial and parallel execution of tasks and provides different methods to retrieve
 * the results of the job execution. The class also handles fail-fast behavior and tracks metrics
 * and tracing information for each job and task execution.
 *
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 2.0
 * @since 2020/9/18 13:41.
 */
@Slf4j(topic = "Job")
public class JobExecutor {

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
    public <Input, Output> void execute(Job<Input, Output> job, Input input) throws JobWrappedMultipleFailureException {
        JobContext<Output> context = new JobContext<>(true, null);
        this.doExecute(job, input, context);
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
    public <Input, Output> List<Output> executeForList(Job<Input, Output> job, Input input) throws JobWrappedMultipleFailureException {
        JobContext<Output> context = new JobContext<>(true, new ConcurrentLinkedQueue<>());

        this.doExecute(job, input, context);
        if (CollectionUtils.isEmpty(context.outputs)) {
            return Collections.emptyList();
        }
        return context.outputs.stream().map(Pair::getRight).collect(Collectors.toList());
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
    public <Input, Output> Map<String, List<Output>> executeForMap(Job<Input, Output> job, Input input) throws JobWrappedMultipleFailureException {
        JobContext<Output> context = new JobContext<>(true, new ConcurrentLinkedQueue<>());

        this.doExecute(job, input, context);

        if (CollectionUtils.isEmpty(context.outputs)) {
            return Collections.emptyMap();
        }
        Map<String, List<Output>> outputMap = new LinkedHashMap<>(context.outputs.size());
        for (Pair<String, Output> output : context.outputs) {
            outputMap.computeIfAbsent(output.getKey(), _K -> new ArrayList<>()).add(output.getValue());
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
    public <Input, Output> JobResult<Output> executeForResult(Job<Input, Output> job, Input input) {
        JobContext<Output> context = new JobContext<>(false, new ConcurrentLinkedQueue<>());

        this.doExecute(job, input, context);

        JobResult<Output> result = new JobResult<>();
        result.setOutputs(Collections.unmodifiableCollection(context.outputs));
        result.setExceptions(Collections.unmodifiableCollection(context.exceptions));
        return result;
    }

    // ---- core helpers ---- //

    /**
     * Executes the job with the given input and context.
     * Handles both serial and parallel execution based on the job configuration.
     *
     * @param job     The job to be executed
     * @param input   The input parameter for the job
     * @param context The context to store execution results and exceptions
     * @throws JobWrappedMultipleFailureException If an exception occurs during job execution
     */
    private <Input, Output> void doExecute(Job<Input, Output> job, Input input, JobContext<Output> context) throws JobWrappedMultipleFailureException {
        Preconditions.checkArgument(job != null);
        Preconditions.checkArgument(config.executor != null || !hasParallelJob(job));
        if (CollectionUtils.isEmpty(job.tasks)) {
            return;
        }

        this.executeJob(job, input, context);
        log.info("Job:[{}({})] Executed={}(Err:{}) Skipped={} Output={} Elapsed={}ms.", job.name, job.parallel ? "P" : "S", context.executed.get(), context.excepted.get(), context.skipped.get(), context.size(), context.cost());

        if (context.throwsExecuteException && CollectionUtils.isNotEmpty(context.exceptions)) {
            tracer.currentSpan().event("JobThrowExceptions", job.name);
            metrics.incCounter("job_throw_exceptions", job.name);
            throw new JobWrappedMultipleFailureException(Message.format("MATRIX-JOB-0000-0000", job.name), context.exceptions.toArray(new Throwable[0]));
        }
    }

    /**
     * Executes the job with the given input and context.
     * Handles both serial and parallel execution based on the job configuration.
     *
     * @param job     The job to be executed
     * @param input   The input parameter for the job
     * @param context The context to store execution results and exceptions
     */
    private <Input, Output> void executeJob(Job<Input, Output> job, Input input, JobContext<Output> context) {
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

    /**
     * Executes the job tasks in serial order.
     *
     * @param job     The job to be executed
     * @param input   The input parameter for the job
     * @param context The context to store execution results and exceptions
     */
    private <Input, Output> void executeSerialJob(Job<Input, Output> job, Input input, JobContext<Output> context) {
        for (Function<Input, Output> t : job.tasks) {
            if (t instanceof Task) {
                this.executeTask(job.name, (Task<Input, Output>) t, input, context);
            } else {
                this.executeJob((Job<Input, Output>) t, input, context);
            }
        }
    }

    /**
     * Executes the job tasks in parallel order.
     *
     * @param job     The job to be executed
     * @param input   The input parameter for the job
     * @param context The context to store execution results and exceptions
     */
    private <Input, Output> void executeParallelJob(Job<Input, Output> job, Input input, JobContext<Output> context) {
        CountDownLatch latch = new CountDownLatch(job.tasks.size());
        for (Function<Input, Output> t : job.tasks) {
            if (t instanceof Task) {
                this.submitTask(job.name, (Task<Input, Output>) t, input, latch, context);
            } else {
                this.submitJob((Job<Input, Output>) t, input, latch, context);
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

    /**
     * Submits a job for parallel execution.
     *
     * @param job     The job to be executed
     * @param input   The input parameter for the job
     * @param latch   The CountDownLatch to synchronize task completion
     * @param context The context to store execution results and exceptions
     */
    private <Input, Output> void submitJob(Job<Input, Output> job, Input input, CountDownLatch latch, JobContext<Output> context) {
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

    /**
     * Submits a task for parallel execution.
     *
     * @param jobName The name of the job containing the task
     * @param task    The task to be executed
     * @param input   The input parameter for the task
     * @param latch   The CountDownLatch to synchronize task completion
     * @param context The context to store execution results and exceptions
     */
    private <Input, Output> void submitTask(String jobName, Task<Input, Output> task, Input input, CountDownLatch latch, JobContext<Output> context) {
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

    /**
     * Executes a single task.
     *
     * @param jobName The name of the job containing the task
     * @param task    The task to be executed
     * @param input   The input parameter for the task
     * @param context The context to store execution results and exceptions
     */
    private <Input, Output> void executeTask(String jobName, Task<Input, Output> task, Input input, JobContext<Output> context) {
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

    /**
     * Checks if the job or any nested jobs contain parallel tasks.
     *
     * @param job The job to check
     * @return true if the job or any nested jobs contain parallel tasks, false otherwise
     */
    private static boolean hasParallelJob(Job<?, ?> job) {
        if (job == null) {
            return false;
        }
        if (job.parallel) {
            return true;
        }
        for (Function<?, ?> t : job.tasks) {
            if (t instanceof Job) {
                if (hasParallelJob((Job<?, ?>) t)) {
                    return true;
                }
            }
        }
        return false;
    }
}