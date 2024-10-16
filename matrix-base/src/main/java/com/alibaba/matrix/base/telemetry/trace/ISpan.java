package com.alibaba.matrix.base.telemetry.trace;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2022/9/5 16:19.
 */
public interface ISpan {

    String STATUS_SUCCESS = "SUCCESS";

    String STATUS_FAILED = "FAILED";

    void event(String type, Object value);

    void setStatus(Throwable t);

    void setStatus(String status);

    void finish();
}
