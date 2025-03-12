package com.alibaba.matrix.extension.test.o;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2023/9/18 21:56.
 */
public class ThreadLocalTest {

    private static final AtomicReference<ExecutorService> normalExecutor = new AtomicReference<>();
    private static final AtomicReference<ExecutorService> ttlExecutor = new AtomicReference<>();

    static {
        Executors.newCachedThreadPool().submit(() -> {
            ttlExecutor.set(TtlExecutors.getTtlExecutorService(Executors.newFixedThreadPool(1)));
        });
        Executors.newCachedThreadPool().submit(() -> {
            normalExecutor.set(Executors.newFixedThreadPool(1));
        });
    }

    public static void main(String[] args) throws InterruptedException {

        Executors.newCachedThreadPool().submit(() -> {
            TransmittableThreadLocal<String> local = new TransmittableThreadLocal<>();
            local.set("hhhh");

            normalExecutor.get().submit(() -> System.out.println(local.get()));
            ttlExecutor.get().submit(() -> System.out.println(local.get()));
        });

        Thread.sleep(1000L);
    }


}
