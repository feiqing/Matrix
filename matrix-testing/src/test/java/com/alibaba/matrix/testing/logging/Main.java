package com.alibaba.matrix.testing.logging;

import com.alibaba.matrix.base.logging.Logging;
import com.alibaba.matrix.base.util.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jimi.zhu@temu.com
 * @version 1.0
 * @since 2024/10/6 21:06.
 */
public class Main {

    static {
        Logging.init("classpath:flow-logback.xml", "classpath:flow-log4j2.xml");
        Logging.init("classpath:extension-logback.xml", "classpath:extension-log4j2.xml");
    }

    private static final Logger flow = LoggerFactory.getLogger("FLOW");

    private static final Logger extension = LoggerFactory.getLogger("EXTENSION");

    public static void main(String[] args) throws InterruptedException {
        flow.info("test");
        extension.info("test");
        Thread.sleep(T.OneM);
    }
}
