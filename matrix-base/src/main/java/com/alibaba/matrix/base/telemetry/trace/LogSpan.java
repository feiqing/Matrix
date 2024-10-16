package com.alibaba.matrix.base.telemetry.trace;

import lombok.extern.slf4j.Slf4j;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2022/9/5 16:19.
 */
@Slf4j(topic = "Tracer")
public class LogSpan implements ISpan {

    private final String type;

    private final String name;

    private String status;

    private Throwable throwable;

    public LogSpan(String type, String name) {
        this.type = type;
        this.name = name;
        log.info("Span[type={}, name={}] start.", type, name);
    }

    @Override
    public void event(String type, Object value) {
        log.info("Span[type={}, name={}] event: type={}, value={}.", this.type, name, type, value);
    }

    @Override
    public void setStatus(Throwable t) {
        this.throwable = t;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public void finish() {
        log.info("Span[type={}, name={}] finished with [{}].", type, name, status == null ? "ER" : status, throwable);
    }
}
