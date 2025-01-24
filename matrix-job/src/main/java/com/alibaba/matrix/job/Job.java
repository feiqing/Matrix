package com.alibaba.matrix.job;

import lombok.Getter;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2020/9/18 13:41.
 */
@Getter
public class Job implements Serializable {

    private static final long serialVersionUID = 6090252744510482638L;

    public final String name;

    public final List<Object> tasks;

    public final boolean parallel;

    public final Long timeout;

    public final TimeUnit unit;

    public Job(String name, List<Object> tasks, boolean parallel, Long timeout, TimeUnit unit) {
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
