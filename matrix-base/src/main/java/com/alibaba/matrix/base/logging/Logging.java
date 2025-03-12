package com.alibaba.matrix.base.logging;

import lombok.extern.slf4j.Slf4j;

/**
 * The Logging class is responsible for initializing logging frameworks based on the provided configuration files.
 * It attempts to initialize Logback and Log4J2 in sequence. If both fail, it logs an error indicating that the default logging will be used.
 *
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2023/7/15 14:45.
 */
@Slf4j(topic = "Logging")
public class Logging {

    /**
     * Initializes the logging framework using the provided configuration files for Logback and Log4J2.
     * It first tries to initialize Logback. If successful, it logs the success message and returns.
     * If Logback initialization fails, it attempts to initialize Log4J2.
     * If both initializations fail, it logs an error indicating that the default logging will be used.
     *
     * @param logbackConfigFile the path to the Logback configuration file
     * @param log4j2ConfigFile  the path to the Log4J2 configuration file
     */
    public static void init(String logbackConfigFile, String log4j2ConfigFile) {
        try {
            Class.forName("ch.qos.logback.classic.Logger");
            log.info("Logging [LOGBACK:({})] init starting...", logbackConfigFile);
            Logback logback = new Logback();
            logback.loadConfiguration(logbackConfigFile);
            log.info("Logging [LOGBACK:({})] init success.", logbackConfigFile);
            return;
        } catch (ClassNotFoundException ignored) {
        } catch (Throwable t) {
            log.error("Logging [LOGBACK:({})] init error.", logbackConfigFile, t);
        }

        try {
            Class.forName("org.apache.logging.log4j.Logger");
            log.info("Logging [LOG4J2:({})] init starting...", log4j2ConfigFile);
            Log4J2 log4j2 = new Log4J2();
            log4j2.loadConfiguration(log4j2ConfigFile);
            log.info("Logging [LOG4J2:({})] init success.", log4j2ConfigFile);
            return;
        } catch (ClassNotFoundException ignored) {
        } catch (Throwable t) {
            log.error("Logging [LOG4J2:({})] init error.", log4j2ConfigFile, t);
        }

        log.error("Logging [{}/{}] init failed, Using [DEFAULT] logging.", logbackConfigFile, log4j2ConfigFile);
    }
}
