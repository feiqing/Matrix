package com.alibaba.matrix.base.telemetry.trace;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/5 15:47.
 */
public interface ITracer {

    ISpan currentSpan();

    ISpan newSpan(String type, String name);

}
