package com.alibaba.matrix.base.logging;

import com.alibaba.matrix.base.logging.log4j2.Log4J2MatrixLogging;
import com.alibaba.matrix.base.logging.logback.LogbackMatrixLogging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatrixLoggingSystem {

    private static final Logger logger = LoggerFactory.getLogger("Matrix");

    public static void initLoggingSystem(String logbackConfigFile, String log4j2ConfigFile) {
        try {
            Class.forName("ch.qos.logback.classic.Logger");
            logger.info("Init MatrixLoggingSystem:[LOGBACK:({})] Starting...", logbackConfigFile);
            LogbackMatrixLogging logging = new LogbackMatrixLogging();
            logging.loadConfiguration(logbackConfigFile);
            logger.info("Init MatrixLoggingSystem:[LOGBACK:({})] Success.", logbackConfigFile);
            return;
        } catch (ClassNotFoundException ignored) {
        } catch (Throwable t) {
            logger.error("Init MatrixLoggingSystem:[LOGBACK:({})] Error.", logbackConfigFile, t);
        }

        try {
            Class.forName("org.apache.logging.log4j.Logger");
            logger.info("Init MatrixLoggingSystem:[LOG4J2:({})] Starting...", log4j2ConfigFile);
            Log4J2MatrixLogging logging = new Log4J2MatrixLogging();
            logging.loadConfiguration(log4j2ConfigFile);
            logger.info("Init MatrixLoggingSystem:[LOG4J2:({})] Success.", log4j2ConfigFile);
            return;
        } catch (ClassNotFoundException ignored) {
        } catch (Throwable t) {
            logger.error("Init MatrixLoggingSystem:[LOG4J2:({})] Error.", log4j2ConfigFile, t);
        }

        logger.warn("Init MatrixLoggingSystem Failed, USING SYSTEM [DEFAULT] LOGGING.");
    }
}
