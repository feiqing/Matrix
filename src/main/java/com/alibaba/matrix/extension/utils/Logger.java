package com.alibaba.matrix.extension.utils;

import com.alibaba.matrix.extension.model.Impl;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/7/10 21:21.
 */
public class Logger {

    public static final org.slf4j.Logger log = LoggerFactory.getLogger("Extension");

    public static String formatDesc(String desc) {
        return StringUtils.isEmpty(desc) ? "''" : desc;
    }

    public static String formatImpl(Impl impl) {
        return String.format("%s:%s(desc:%s/priority:%s)", impl.type, getInstanceDesc(impl), formatDesc(impl.desc), impl.priority);
    }

    private static Object getInstanceDesc(Impl impl) {
        if (impl.instance != null) {
            return impl.instance;
        }

        if (impl.isLazy()) {
            return "[LZY]";
        }

        return "[ERR]";
    }
}
