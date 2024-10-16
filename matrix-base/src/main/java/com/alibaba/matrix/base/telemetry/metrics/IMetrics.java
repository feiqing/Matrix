package com.alibaba.matrix.base.telemetry.metrics;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2022/9/5 16:19.
 */
public interface IMetrics {

    default void incCounter(String type, String name) {
        incCounter(type, name, 1.0);
    }

    void incCounter(String type, String name, double value);
}
