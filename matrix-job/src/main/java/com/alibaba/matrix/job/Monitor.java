package com.alibaba.matrix.job;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static com.alibaba.matrix.base.telemetry.TelemetryProvider.metrics;
import static com.alibaba.matrix.base.telemetry.TelemetryProvider.tracer;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2020/9/18 13:41.
 */
@Slf4j
public class Monitor {

    private static final String BUSINESS = "JobExecutorBusiness";

    private static final String ALARM = "JobExecutorAlarm";

    public static void monitorBusiness(String message) {
        try {
            if (StringUtils.isBlank(message)) {
                return;
            }
            tracer.currentSpan().logEvent(BUSINESS, message);
            metrics.incCounter("job_executor_business_data", message);
        } catch (Throwable t) {
            log.error("monitorBusiness error.", t);
        }
    }

    public static void monitorAlarm(String message) {
        try {
            if (StringUtils.isBlank(message)) {
                return;
            }
            tracer.currentSpan().logEvent(ALARM, message);
            metrics.incCounter("job_executor_alarm_data", message);
        } catch (Throwable t) {
            log.error("monitorAlarm error.", t);
        }
    }
}
