package com.alibaba.matrix.extension.plugin;

import com.alibaba.matrix.base.logging.MatrixLoggingSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/15 20:25.
 */
public class ExtensionIndependentLoggingPlugin extends ExtensionLoggingPlugin {

    private static final Logger logger;

    static {
        MatrixLoggingSystem.initLoggingSystem("classpath:extension-logback.xml", "classpath:extension-log4j2.xml");
        logger = LoggerFactory.getLogger("EXTENSION");
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
