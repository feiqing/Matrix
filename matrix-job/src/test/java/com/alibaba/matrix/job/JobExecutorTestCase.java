package com.alibaba.matrix.job;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2024/10/18 16:11.
 */
public class JobExecutorTestCase {

    private static final JobExecutor executor = new JobExecutor(Executors.newCachedThreadPool());

    @Test(expected = JobWrappedMultipleFailureException.class)
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
        } catch (JobWrappedMultipleFailureException e) {
            ExceptionUtils.rethrow(e.getCauses()[0]);
        }
    }

    @Test
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

        Map<String, List<Object>> myJob = executor.executeForMap(builder.buildSerialJob("MyJob"), null);
        System.out.println(myJob);
    }

    @Test
    public void test_task_execute_parallel() {
        Job job = Job.newBuilder().addTask(o -> {
            return 1;
        }).buildParallelJob("MyJob", 1, TimeUnit.MILLISECONDS);

        executor.execute(job, null);
    }

    @Test
    public void test_base_execute_for_list() {
        Job.Builder<Object, Object> jobBuilder = Job.newBuilder();
        for (int i = 0; i < 10; ++i) {
            int finalI = i;
            jobBuilder.addTask(o -> finalI);
        }

        List<Object> objects = executor.executeForList(jobBuilder.buildParallelJob("test_base_execute_for_list", 1, TimeUnit.SECONDS), null);
        System.out.println(objects);
    }

    @Test
    public void test_base_execute_for_list_serial() {
        Job.Builder<Object, Object> jobBuilder = Job.newBuilder();
        for (int i = 0; i < 10; ++i) {
            int finalI = i;
            jobBuilder.addTask(o -> finalI);
        }

        List<Object> objects = executor.executeForList(jobBuilder.buildSerialJob("test_base_execute_for_list_serial"), null);
        System.out.println(objects);
    }

    @Test
    public void test_base_execute_for_map() {
        Job.Builder<Object, Object> jobBuilder = Job.newBuilder();
        for (int i = 0; i < 10; ++i) {
            int finalI = i;
            jobBuilder.addTask(o -> finalI);
        }

        Map<String, List<Object>> map = executor.executeForMap(jobBuilder.buildParallelJob("test_base_execute_for_map", 1, TimeUnit.SECONDS), null);
        System.out.println(map);
    }
}
