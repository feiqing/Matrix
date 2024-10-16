package com.alibaba.matrix.base.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.util.Map;

/**
 * Support for Log4j version 2.7 or higher
 *
 * @author <a href="mailto:huangxiaoyu1018@gmail.com">hxy1991</a>
 * @since 0.9.0
 */
public class Log4J2 {

    private static final String LOG4J2_LOCATION_DEMO = "classpath:matrix-log4j2.xml";

    public void loadConfiguration(String configFile) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = context.getConfiguration();

        Configuration fileConfiguration = loadFileConfiguration(context, configFile);
        fileConfiguration.start();

        // append logger and appender to configuration
        for (Appender appender : fileConfiguration.getAppenders().values()) {
            configuration.addAppender(appender);
        }
        for (Map.Entry<String, LoggerConfig> entry : fileConfiguration.getLoggers().entrySet()) {
            configuration.addLogger(entry.getKey(), entry.getValue());
        }
        context.updateLoggers();
    }

    private Configuration loadFileConfiguration(LoggerContext context, String configFile) {
        try {
            ConfigurationSource source = new ConfigurationSource(new PathMatchingResourcePatternResolver().getResource(configFile).getInputStream());
            // since log4j 2.7 getConfiguration(LoggerContext context, ConfigurationSource source)
            return ConfigurationFactory.getInstance().getConfiguration(context, source);
        } catch (Exception e) {
            throw new IllegalStateException("Could not initialize Log4J2 logging from " + configFile, e);
        }
    }
}
