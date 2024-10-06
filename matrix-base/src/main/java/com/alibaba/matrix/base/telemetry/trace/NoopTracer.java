package com.alibaba.matrix.base.telemetry.trace;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2022/9/5 16:19.
 */
public class NoopTracer implements ITracer {

    private static final ISpan SPAN = new NoopSpan();

    @Override
    public ISpan currentSpan() {
        return SPAN;
    }

    @Override
    public ISpan newSpan(String type, String name) {
        return SPAN;
    }
}
