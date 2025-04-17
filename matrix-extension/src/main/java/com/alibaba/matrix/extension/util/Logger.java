package com.alibaba.matrix.extension.util;

import com.alibaba.matrix.extension.core.ExtensionExecuteContext;
import com.alibaba.matrix.extension.core.ExtensionImplEntity;
import com.alibaba.matrix.extension.core.config.ExtensionImpl;
import com.google.common.base.Strings;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class Logger {

    public static final org.slf4j.Logger log = LoggerFactory.getLogger("Extension");

    public static String formatExt(Class<?> ext, String desc) {
        return String.format("%s(desc:%s)", ext.getName(), getDesc(desc));
    }

    public static String formatBase(Object base) {
        // return String.format("%s(type:%s)", base, base.getClass().getName());
        return base.toString();
    }

    public static String formatImpl(ExtensionImpl impl) {
        return String.format("%s:%s(desc:%s/priority:%s)", impl.type, getImplDesc(impl), getDesc(impl.desc), impl.priority);
    }

    public static String formatExec(ExtensionExecuteContext ctx, ExtensionImplEntity impl) {
        return String.format("%s:%s(%s)", impl.namespace, impl.code, impl.priority);
    }

    private static Object getImplDesc(ExtensionImpl impl) {
        if (impl.instance != null) {
            return impl.instance;
        }

        if (impl.lazy) {
            return "[LZ]";
        }

        return "[ER]";
    }

    public static String getDesc(String desc) {
        return Strings.isNullOrEmpty(desc) ? "''" : desc;
    }
}
