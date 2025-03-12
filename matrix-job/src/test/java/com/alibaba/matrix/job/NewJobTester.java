package com.alibaba.matrix.job;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2025/3/12 13:53.
 */
public class NewJobTester {

    public static void main(String[] args) {
        Task<Object, Object> task = o -> {
            System.out.println("hh");
            return null;
        };

        Job job = Job.newParallelJob("test", 1000L, TimeUnit.MILLISECONDS, task, task, task);

        Job job1 = Job.newSerialJob("test", task, task, task);

        Job job2 = Job.newParallelJob("test", 1000L, TimeUnit.MILLISECONDS, Arrays.asList(task, task, task));

        Job job3 = Job.newSerialJob("test", Arrays.asList(task, task, task));

        Job job4 = Job.newParallelJob("test", 1000L, TimeUnit.MILLISECONDS, task, task, task);

        Job job5 = Job.newSerialJob("jo", job1, job4, job3);

        Job job6 = Job.newSerialJob("jo", job1, job4, job3);

        Job job7 = Job.newParallelJob("jo", 1000L, TimeUnit.MILLISECONDS, Arrays.asList(job6, job5, task));

        JobExecutor executor = new JobExecutor(ForkJoinPool.commonPool());
        executor.execute(job7, null);
        executor.executeForMap(job, null);
    }
}
