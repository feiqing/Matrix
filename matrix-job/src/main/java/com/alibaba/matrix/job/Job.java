package com.alibaba.matrix.job;

import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The Job class represents a job that consists of a list of tasks or sub-jobs.
 * It supports both serial and parallel execution of tasks and can have a specified timeout.
 *
 * @param <Input>  the input type for the tasks
 * @param <Output> the output type for the tasks
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2020/9/18 13:41.
 */
@Getter
public class Job<Input, Output> implements Serializable, Function<Input, Output> {

    private static final long serialVersionUID = 6090252744510482638L;

    /**
     * The name of the job, used to identify the job.
     */
    final String name;

    /**
     * The list of tasks or sub-jobs that make up the job.
     */
    final List<Function<Input, Output>> tasks;

    /**
     * Indicates whether the job should be executed in parallel.
     */
    final boolean parallel;

    /**
     * The timeout for the job execution, if applicable.
     */
    final Long timeout;

    /**
     * The time unit for the timeout, if applicable.
     */
    final TimeUnit unit;

    /**
     * Constructs a new Job with the specified parameters.
     *
     * @param name     the name of the job
     * @param tasks    the list of tasks or sub-jobs
     * @param parallel whether the job should be executed in parallel
     * @param timeout  the timeout for the job execution, if applicable
     * @param unit     the time unit for the timeout, if applicable
     */
    public Job(String name, List<Function<Input, Output>> tasks, boolean parallel, Long timeout, TimeUnit unit) {
        this.name = name;
        this.tasks = Collections.unmodifiableList(tasks);
        this.parallel = parallel;
        this.timeout = timeout;
        this.unit = unit;
    }

    /**
     * Creates a new parallel job with the specified tasks and timeout.
     *
     * @param name    the name of the job
     * @param timeout the timeout for the job execution
     * @param unit    the time unit for the timeout
     * @param tasks   the array of tasks
     * @return a new parallel job
     */
    public static <Input, Output> Job<Input, Output> newParallelJob(String name, Long timeout, TimeUnit unit, Task<Input, Output>... tasks) {
        return newParallelJob(name, timeout, unit, Arrays.stream(tasks).collect(Collectors.toList()));
    }

    /**
     * Creates a new parallel job with the specified sub-jobs and timeout.
     *
     * @param name    the name of the job
     * @param timeout the timeout for the job execution
     * @param unit    the time unit for the timeout
     * @param jobs    the array of sub-jobs
     * @return a new parallel job
     */
    public static <Input, Output> Job<Input, Output> newParallelJob(String name, Long timeout, TimeUnit unit, Job<Input, Output>... jobs) {
        return newParallelJob(name, timeout, unit, Arrays.stream(jobs).collect(Collectors.toList()));
    }

    /**
     * Creates a new parallel job with the specified parameters.
     *
     * @param name    The name of the job, used to identify the job.
     * @param timeout The maximum time allowed for the job execution. If the job does not complete within this time, it may be terminated or considered failed.
     * @param unit    The unit of time for the timeout parameter, such as SECONDS, MINUTES, etc.
     * @param tasks   A list of objects representing the tasks or jobs that make up the job.
     * @return A new Job instance configured for parallel execution.
     */
    public static <Input, Output> Job<Input, Output> newParallelJob(String name, Long timeout, TimeUnit unit, List<Function<Input, Output>> tasks) {
        return new Job<>(name, Collections.unmodifiableList(tasks), true, timeout, unit);
    }

    /**
     * Creates a new serial job with the specified tasks.
     *
     * @param name  the name of the job
     * @param tasks the array of tasks
     * @return a new serial job
     */
    public static <Input, Output> Job<Input, Output> newSerialJob(String name, Task<Input, Output>... tasks) {
        return newSerialJob(name, Arrays.stream(tasks).collect(Collectors.toList()));
    }

    /**
     * Creates a new serial job with the specified sub-jobs.
     *
     * @param name the name of the job
     * @param jobs the array of sub-jobs
     * @return a new serial job
     */
    public static <Input, Output> Job<Input, Output> newSerialJob(String name, Job<Input, Output>... jobs) {
        return newSerialJob(name, Arrays.stream(jobs).collect(Collectors.toList()));
    }

    /**
     * Creates a new serial job with the specified parameters.
     *
     * @param name  The name of the job, used to identify the job.
     * @param tasks A list of objects representing the tasks or jobs that make up the job.
     *              These tasks will be executed in the order they appear in the list.
     * @return A new Job instance configured for serial execution.
     * The timeout and time unit are set to null since this method does not support specifying a timeout.
     */
    public static <Input, Output> Job<Input, Output> newSerialJob(String name, List<Function<Input, Output>> tasks) {
        return new Job<>(name, Collections.unmodifiableList(tasks), false, null, null);
    }

    /**
     * Returns a new Builder instance for constructing a Job.
     *
     * @param <Input>  the input type for the tasks
     * @param <Output> the output type for the tasks
     * @return a new Builder instance
     */
    public static <Input, Output> Builder<Input, Output> newBuilder() {
        return new Builder<>();
    }

    /**
     * The Builder class provides a fluent API for constructing Job instances.
     *
     * @param <Input>  the input type for the tasks
     * @param <Output> the output type for the tasks
     */
    public static class Builder<Input, Output> {

        /**
         * The list of tasks or sub-jobs being built.
         */
        private final List<Function<Input, Output>> tasks = new LinkedList<>();

        /**
         * Adds a task to the job.
         *
         * @param task the task to add
         * @return the Builder instance for method chaining
         */
        public Builder<Input, Output> addTask(Task<Input, Output> task) {
            tasks.add(task);
            return this;
        }

        /**
         * Adds a sub-job to the job.
         *
         * @param job the sub-job to add
         * @return the Builder instance for method chaining
         */
        public Builder<Input, Output> addJob(Job<Input, Output> job) {
            tasks.add(job);
            return this;
        }

        /**
         * Builds a new serial job with the specified name.
         *
         * @param name the name of the job
         * @return a new serial job
         */
        public Job<Input, Output> buildSerialJob(String name) {
            return new Job<>(name, Collections.unmodifiableList(tasks), false, null, null);
        }

        /**
         * Builds a new parallel job with the specified name and timeout.
         *
         * @param name    the name of the job
         * @param timeout the timeout for the job execution
         * @param unit    the time unit for the timeout
         * @return a new parallel job
         */
        public Job<Input, Output> buildParallelJob(String name, long timeout, TimeUnit unit) {
            return new Job<>(name, Collections.unmodifiableList(tasks), true, timeout, unit);
        }
    }

    @Override
    public Output apply(Input input) {
        throw new UnsupportedOperationException("Job is not a function");
    }
}

