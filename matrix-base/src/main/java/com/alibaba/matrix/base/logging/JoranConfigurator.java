package com.alibaba.matrix.base.logging;

import ch.qos.logback.core.joran.event.SaxEvent;

import java.util.List;

/**
 * ensure that Letter configuration does not affect user configuration savepoints and  scanning url.
 *
 * @author <a href="mailto:hujun3@xiaomi.com">hujun</a>
 * @see <a href="https://github.com/alibaba/nacos/issues/6999">#6999</a>
 */
public class JoranConfigurator extends ch.qos.logback.classic.joran.JoranConfigurator {

    /**
     * ensure that Letter configuration does not affect user configuration savepoints.
     *
     * @param eventList safe data
     */
    @Override
    public void registerSafeConfiguration(List<SaxEvent> eventList) {
    }
}
