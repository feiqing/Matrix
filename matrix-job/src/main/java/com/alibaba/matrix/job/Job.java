package com.alibaba.matrix.job;

import lombok.Getter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2019/9/8 13:44.
 */
@Getter
public class Job {

    protected final String name;

    protected final List<Object> tasks;

    protected final boolean parallel;

    protected final Long timeout;

    protected final TimeUnit unit;

    private Job(String name, List<Object> tasks, boolean parallel, Long timeout, TimeUnit unit) {
        this.name = name;
        this.tasks = tasks;
        this.parallel = parallel;
        this.timeout = timeout;
        this.unit = unit;
    }

    public static <Input, Output> Builder<Input, Output> newBuilder() {
        return new Builder<>();
    }

    public static class Builder<Input, Output> {

        private final List<Object> tasks = new LinkedList<>();

        public Builder<Input, Output> addTask(Task<Input, Output> task) {
            tasks.add(task);
            return this;
        }

        public Builder<Input, Output> addJob(Job job) {
            tasks.add(job);
            return this;
        }

        public Job buildSerialJob(String name) {
            return new Job(name, Collections.unmodifiableList(tasks), false, null, null);
        }

        public Job buildParallelJob(String name, long timeout, TimeUnit unit) {
            return new Job(name, Collections.unmodifiableList(tasks), true, timeout, unit);
        }
    }
}
