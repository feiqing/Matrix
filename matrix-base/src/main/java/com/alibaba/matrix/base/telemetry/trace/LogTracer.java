package com.alibaba.matrix.base.telemetry.trace;

import lombok.extern.slf4j.Slf4j;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2022/9/5 16:19.
 */
@Slf4j(topic = "Tracer")
public class LogTracer implements ITracer {

    private ISpan currentSpan = new LogSpan("ROOT", "root");

    @Override
    public ISpan currentSpan() {
        return currentSpan;
    }

    @Override
    public ISpan newSpan(String type, String name) {
        return (currentSpan = new LogSpan(type, name));
    }
}
