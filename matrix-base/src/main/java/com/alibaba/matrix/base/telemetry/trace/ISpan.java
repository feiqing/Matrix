package com.alibaba.matrix.base.telemetry.trace;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/5 15:47.
 */
public interface ISpan {

    String STATUS_SUCCESS = "SUCCESS";

    String STATUS_FAILED = "FAILED";

    void logEvent(String key, Object value);

    void setStatus(Throwable t);

    void setStatus(String status);

    void finish();
}
