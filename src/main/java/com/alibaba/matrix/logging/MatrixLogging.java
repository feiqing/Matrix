package com.alibaba.matrix.logging;

import com.alibaba.matrix.logging.log4j2.Log4J2MatrixLogging;
import com.alibaba.matrix.logging.logback.LogbackMatrixLogging;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Slf4j(topic = "Matrix")
public class MatrixLogging {

    public static final Logger extensionLogger;

    public static final Logger flowLogger;

    static {
        initLoggingSystem();
        extensionLogger = LoggerFactory.getLogger("extension");
        flowLogger = LoggerFactory.getLogger("flow");
    }


    private static void initLoggingSystem() {
        try {
            Class.forName("ch.qos.logback.classic.Logger");
            LogbackMatrixLogging logging = new LogbackMatrixLogging();
            logging.loadConfiguration();
            log.info("init MatrixLoggingSystem:[logback] success.");
            return;
        } catch (ClassNotFoundException ignored) {
        } catch (Throwable t) {
            log.error("init MatrixLoggingSystem:[logback] error.", t);
        }

        try {
            Class.forName("org.apache.logging.log4j.Logger");
            Log4J2MatrixLogging logging = new Log4J2MatrixLogging();
            logging.loadConfiguration();
            log.info("init MatrixLoggingSystem:[log4j2] success.");
            return;
        } catch (ClassNotFoundException ignored) {
        } catch (Throwable t) {
            log.error("init MatrixLoggingSystem:[log4j2] error.", t);
        }

        log.warn("init MatrixLoggingSystem failed, using system [default] logger.");
    }
}
