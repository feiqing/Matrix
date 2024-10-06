package com.alibaba.matrix.base.telemetry.metrics;

import java.util.Map;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2022/9/5 16:19.
 */
public class NoopMetrics implements IMetrics {

    @Override
    public void incCounter(String type, String name, String desc, double value, Map<String, Object> attributes) {
    }
}
