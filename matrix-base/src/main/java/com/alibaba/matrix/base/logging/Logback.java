package com.alibaba.matrix.base.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * Support for Logback version 1.0.8 or higher
 *
 * @author <a href="mailto:huangxiaoyu1018@gmail.com">hxy1991</a>
 * @author <a href="mailto:hujun3@xiaomi.com">hujun</a>
 * @since 0.9.0
 */
public class Logback {

    private static final String LOGBACK_LOCATION_DEMO = "classpath:matrix-logback.xml";

    public void loadConfiguration(String configFile) {
        LoggerContext context = loadingConfiguration(configFile);
        if (context.getObject("RECONFIGURE_ON_CHANGE_TASK") != null && context.getCopyOfListenerList().stream().noneMatch(listener -> listener instanceof LoggerContextListener)) {
            context.addListener(new LoggerContextListener(configFile));
        }
    }

    private LoggerContext loadingConfiguration(String configFile) {
        try {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            JoranConfigurator configAdapter = new JoranConfigurator();
            configAdapter.setContext(context);
            configAdapter.doConfigure(new PathMatchingResourcePatternResolver().getResource(configFile).getInputStream());
            return context;
        } catch (Exception e) {
            throw new IllegalStateException("Could not initialize Logback logging from " + configFile, e);
        }
    }

    public class LoggerContextListener implements ch.qos.logback.classic.spi.LoggerContextListener {

        private final String configFile;

        public LoggerContextListener(String configFile) {
            this.configFile = configFile;
        }

        @Override
        public boolean isResetResistant() {
            return true;
        }

        @Override
        public void onReset(LoggerContext context) {
            loadingConfiguration(configFile);
        }

        @Override
        public void onStart(LoggerContext context) {
        }

        @Override
        public void onStop(LoggerContext context) {
        }

        @Override
        public void onLevelChange(Logger logger, Level level) {
        }
    }

}
