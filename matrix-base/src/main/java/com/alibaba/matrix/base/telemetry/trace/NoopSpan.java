package com.alibaba.matrix.base.telemetry.trace;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/5 16:23.
 */
public class NoopSpan implements ISpan {

    @Override
    public void logEvent(String key, Object value) {

    }

    @Override
    public void setStatus(Throwable t) {

    }

    @Override
    public void setStatus(String status) {

    }

    @Override
    public void finish() {

    }
}