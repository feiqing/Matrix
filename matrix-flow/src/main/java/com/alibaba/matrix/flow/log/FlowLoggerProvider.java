package com.alibaba.matrix.flow.log;

import com.alibaba.matrix.base.util.MatrixServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/8/28 11:32.
 */
public class FlowLoggerProvider {

    public static Logger logger;

//    static {
//        MatrixLoggingSystem.initLoggingSystem("classpath:matrix-flow-logback.xml", "classpath:matrix-flow-log4j2.xml");
//        logger = MatrixServiceLoader.load(Logger.class, LoggerFactory.getLogger("FLOW"));
//    }

    static {
        logger = MatrixServiceLoader.loadService(Logger.class, LoggerFactory.getLogger("FLOW"));
    }

}
