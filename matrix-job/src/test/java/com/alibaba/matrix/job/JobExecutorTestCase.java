package com.alibaba.matrix.job;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2024/10/18 16:11.
 */
public class JobExecutorTestCase {

    private static final JobExecutor<Object, Object> executor = new JobExecutor<>(Executors.newCachedThreadPool());

    @Test(expected = JobExecuteException.class)
    public void test_task_execute_error() {
        Job job = Job.newBuilder().addTask(o -> {
            throw new RuntimeException("test_task");
        }).buildSerialJob("MyJob");

        executor.execute(job, null);
    }

    @Test(expected = RuntimeException.class)
    public void test_task_execute_error_2() {
        Job job = Job.newBuilder().addTask(o -> {
            throw new RuntimeException("test_task");
        }).buildSerialJob("MyJob");

        try {
            executor.execute(job, null);
        } catch (JobExecuteException e) {
            ExceptionUtils.rethrow(e.getCauses()[0]);
        }
    }

    @Test(expected = JobExecuteException.class)
    public void test_task_duplicate_key() {
        Job.Builder<Object, Object> builder = Job.newBuilder();
        builder.addTask(new Task<Object, Object>() {
            @Override
            public Object execute(Object o) {
                return "value1";
            }

            @Override
            public String key(Object o) {
                return "key1";
            }
        });
        builder.addTask(new Task<Object, Object>() {
            @Override
            public Object execute(Object o) {
                return "value1";
            }

            @Override
            public String key(Object o) {
                return "key1";
            }
        });

        executor.executeForMap(builder.buildSerialJob("MyJob"), null);
    }

    @Test
    public void test_task_execute_parallel() {
        Job job = Job.newBuilder().addTask(o -> {
            return 1;
        }).buildParallelJob("MyJob", 1, TimeUnit.MILLISECONDS);

        executor.execute(job, null);
    }
}
