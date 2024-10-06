package com.alibaba.matrix.base.telemetry.trace;

import lombok.extern.slf4j.Slf4j;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2022/9/5 16:19.
 */
@Slf4j(topic = "Tracer")
public class NoopSpan implements ISpan {

    @Override
    public void logEvent(String key, Object value) {
        log.info("event: {} = {}", key, value);
    }

    @Override
    public void setStatus(Throwable t) {
        log.error("status.", t);
    }

    @Override
    public void setStatus(String status) {
        log.info("status:{}", status);
    }

    @Override
    public void finish() {
        log.info("finish");
    }
}
