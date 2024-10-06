package com.alibaba.matrix.base.telemetry.metrics;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2022/9/5 16:19.
 */
@Slf4j(topic = "Metrics")
public class NoopMetrics implements IMetrics {

    private static final Map<String, AtomicDouble> counter = new ConcurrentHashMap<>();

    @Override
    public void incCounter(String type, String name, String desc, double value, Map<String, Object> attributes) {
        double count = counter.computeIfAbsent(String.format("%s#%s", type, name), k -> new AtomicDouble()).addAndGet(value);
        log.info("counter:[{}-{}({}):{} ({})", type, name, desc, count, attributes);
    }
}
