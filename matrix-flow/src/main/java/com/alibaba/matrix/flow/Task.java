package com.alibaba.matrix.flow;

import com.alibaba.matrix.base.telemetry.trace.ISpan;
import com.alibaba.matrix.flow.util.Message;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.alibaba.matrix.base.telemetry.TelemetryProvider.metrics;
import static com.alibaba.matrix.base.telemetry.TelemetryProvider.tracer;

/**
 * Represents a task in the Matrix flow processing system.
 * Each task can have setup, execution, and teardown phases.
 * The interface provides methods to define these phases and to navigate to the next task in the flow.
 *
 * @param <InputData>  the type of input data for the task
 * @param <OutputData> the type of output data for the task
 *
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2018/9/18 15:47.
 */
public interface Task<InputData, OutputData> {

    Logger logger = LoggerFactory.getLogger("FLOW");

    /**
     * Sets up the task with the provided input data.
     * This method is called before the task is executed.
     *
     * @param inputData the input data for the task
     */
    default void setUp(InputData inputData) {
    }

    /**
     * Executes the task with the provided input data.
     * This method contains the main logic of the task.
     *
     * @param inputData the input data for the task
     * @return the output data produced by the task
     */
    OutputData execute(InputData inputData);

    /**
     * Tears down the task after execution.
     * This method is called after the task is executed, regardless of whether it succeeded or failed.
     *
     * @param inputData  the input data for the task
     * @param outputData the output data produced by the task
     */
    default void tearDown(InputData inputData, OutputData outputData) {
    }

    /**
     * Returns the name of the task.
     * By default, it returns the fully qualified class name of the task.
     *
     * @return the name of the task
     */
    default String name() {
        return this.getClass().getName();
    }

    /**
     * Proceeds to the next task in the flow.
     * This method handles the transition to the next task, including setup, execution, and teardown phases.
     * It also manages telemetry and logging for the task execution.
     *
     * @return the output data produced by the next task
     * @throws IllegalStateException if there are no more tasks in the flow
     */
    default OutputData next() {
        Flow.Meta meta = Flow.getMeta(this);
        Preconditions.checkState(meta != null);
        // The end has been reached
        if (!(++meta.idx < meta.tasks.length)) {
            throw new IllegalStateException(Message.format("MATRIX-FLOW-0000-0001", meta.name));
        }

        Task<InputData, OutputData> next = (Task<InputData, OutputData>) meta.tasks[meta.idx];
        InputData inputData = (InputData) meta.inputData;
        String name = String.format("'%s:%d/%d:%s'", meta.name, meta.idx + 1, meta.tasks.length, next.name());

        metrics.incCounter("execute_flow_task", name);
        ISpan span = tracer.newSpan("FlowTask", name);
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

            span.setStatus(ISpan.STATUS_SUCCESS);

            return outputData;
        } catch (Throwable t) {
            span.setStatus(t);

            span.event("ExecuteFlowTaskError", name);
            metrics.incCounter("execute_flow_task_error", name);
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

