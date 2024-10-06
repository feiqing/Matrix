package com.alibaba.matrix.flow;

import com.alibaba.matrix.base.message.Message;
import com.alibaba.matrix.base.telemetry.trace.ISpan;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.alibaba.matrix.base.telemetry.TelemetryProvider.metrics;
import static com.alibaba.matrix.base.telemetry.TelemetryProvider.tracer;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2018/9/18 15:47.
 */
public interface Task<InputData, OutputData> {

    Logger logger = LoggerFactory.getLogger("FLOW");

    default void setUp(InputData inputData) {
    }

    OutputData execute(InputData inputData);

    default void tearDown(InputData inputData, OutputData outputData) {
    }

    default String name() {
        return this.getClass().getName();
    }

    default OutputData next() {
        Flow.Meta meta = Flow.getMeta(this);
        Preconditions.checkState(meta != null);
        // 已经到达终点
        if (!(++meta.idx < meta.tasks.length)) {
            throw new IllegalStateException(Message.of("MATRIX-FLOW-0000-0001", meta.name).getMessage());
        }

        Task<InputData, OutputData> next = (Task<InputData, OutputData>) meta.tasks[meta.idx];
        InputData inputData = (InputData) meta.inputData;
        String name = String.format("'%s:%d/%d:%s'", meta.name, meta.idx + 1, meta.tasks.length, next.name());

        ISpan span = tracer.newSpan("ExecuteFlowTask", name);
        logger.info("{} start.", name);

        try {
            meta.addAttribute(name, System.currentTimeMillis());

            OutputData outputData = null;
            try {
                next.setUp(inputData);
                outputData = next.execute(inputData);
            } finally {
                next.tearDown(inputData, outputData);
            }

            metrics.incCounter("execute_flow_task_success", name);
            span.setStatus(ISpan.STATUS_SUCCESS);

            return outputData;
        } catch (Throwable t) {

            metrics.incCounter("execute_flow_task_failed", name);
            span.setStatus(t);
            logger.error("{} execute error.", name, t);

            return ExceptionUtils.rethrow(t);
        } finally {

            long total = System.currentTimeMillis() - (long) meta.removeAttribute(name);
            logger.info("{} end {}/{}.", name, total - meta.total, total);
            meta.total = total;

            span.finish();
        }
    }
}
