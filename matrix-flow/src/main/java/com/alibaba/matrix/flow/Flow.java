package com.alibaba.matrix.flow;

import com.alibaba.matrix.base.telemetry.trace.ISpan;
import com.alibaba.matrix.flow.util.Message;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.alibaba.matrix.base.telemetry.TelemetryProvider.metrics;
import static com.alibaba.matrix.base.telemetry.TelemetryProvider.tracer;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2018/9/18 16:01.
 */
@Slf4j(topic = "Flow")
// @SuppressWarnings({"rawtypes", "unchecked"})
public class Flow<InputData, OutputData> {

    private static final ConcurrentMap<Task<?, ?>, String> task2flow = new ConcurrentHashMap<>();

    protected static final ThreadLocal<Map<String, Meta>> metaMap = ThreadLocal.withInitial(HashMap::new);

    @Getter
    private final String name;

    private final Task<InputData, OutputData>[] tasks;

    public Flow(String name, List<Task<InputData, OutputData>> tasks) {
        this(name, tasks.toArray(new Task[0]));
    }

    public Flow(String name, Task<InputData, OutputData>[] tasks) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        Preconditions.checkArgument(ArrayUtils.isNotEmpty(tasks));

        this.name = name;
        this.tasks = tasks;

        for (Task<InputData, OutputData> task : this.tasks) {
            task2flow.put(task, name);
            log.info("Load flow:[{}] task:[{}].", name, task.name());
        }

        log.info("[Matrix-Flow] load flow:[{}] success.", name);
    }

    public OutputData execute(InputData inputData) {
        Map<String, Meta> map = metaMap.get();
        if (map.containsKey(name)) {
            throw new IllegalStateException(Message.format("MATRIX-FLOW-0000-0000", name));
        }

        metrics.incCounter("execute_flow", name);
        ISpan span = tracer.newSpan("Flow", name);
        map.put(name, new Meta(name, tasks, inputData));
        try {
            OutputData outputData = tasks[0].next();
            span.setStatus(ISpan.STATUS_SUCCESS);
            return outputData;
        } catch (Throwable t) {
            span.setStatus(t);

            span.event("ExecuteFlowError", name);
            metrics.incCounter("execute_flow_error", name);
            log.error("Flow {} execute error.", name, t);

            return ExceptionUtils.rethrow(t);
        } finally {
            map.remove(name);
            span.finish();
        }
    }

    protected static Meta getMeta(Task<?, ?> task) {
        String name = task2flow.get(task);
        return metaMap.get().get(name);
    }

    public void clear() {
        metaMap.remove();
    }

    protected static class Meta {

        protected final String name;

        protected final Task<?, ?>[] tasks;

        protected final Object inputData;

        protected int idx = -1;

        protected long total = 0;

        private Map<String, Object> attributes;

        public Meta(String name, Task<?, ?>[] tasks, Object inputData) {
            this.name = name;
            this.tasks = tasks;
            this.inputData = inputData;
        }

        public Map<String, Object> getAttributes() {
            if (this.attributes == null) {
                this.attributes = new HashMap<>();
            }
            return this.attributes;
        }

        public void addAttribute(String key, Object value) {
            getAttributes().put(key, value);
        }

        public Object getAttribute(String key) {
            return getAttributes().get(key);
        }

        public Object removeAttribute(String key) {
            return getAttributes().remove(key);
        }
    }
}
