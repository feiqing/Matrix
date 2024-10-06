package com.alibaba.matrix.base.logging;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/7/15 14:45.
 */
@Slf4j
public class Logging {

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
