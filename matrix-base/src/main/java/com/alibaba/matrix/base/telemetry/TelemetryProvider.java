package com.alibaba.matrix.base.telemetry;

import com.alibaba.matrix.base.telemetry.metrics.IMetrics;
import com.alibaba.matrix.base.telemetry.metrics.NoopMetrics;
import com.alibaba.matrix.base.telemetry.trace.NoopTracer;
import com.alibaba.matrix.base.telemetry.trace.ITracer;
import com.alibaba.matrix.base.util.MatrixServiceLoader;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/5 16:19.
 */
public class TelemetryProvider {

    public static ITracer tracer;

    public static IMetrics metrics;

    static {
        tracer = MatrixServiceLoader.loadService(ITracer.class, new NoopTracer());
        metrics = MatrixServiceLoader.loadService(IMetrics.class, new NoopMetrics());
    }
}
