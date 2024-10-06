package com.alibaba.matrix.extension.util;

import com.alibaba.matrix.extension.model.ExtExecCtx;
import com.alibaba.matrix.extension.model.ExtImpl;
import com.alibaba.matrix.extension.model.Impl;
import com.google.common.base.Strings;
import org.slf4j.LoggerFactory;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
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

    public static String formatImpl(Impl impl) {
        return String.format("%s:%s(desc:%s/priority:%s)", impl.type, getImplDesc(impl), getDesc(impl.desc), impl.priority);
    }

    public static String formatExec(ExtExecCtx ctx, ExtImpl impl) {
        return String.format("%s#%s#%s#%s#%s(%s)", ctx.scope, ctx.code, ctx.ext.getName(), impl.type, impl.instance, getDesc(impl.desc));
    }

    private static Object getImplDesc(Impl impl) {
        if (impl.instance != null) {
            return impl.instance;
        }

        if (impl.isLazy()) {
            return "[LZ]";
        }

        return "[ER]";
    }

    private static String getDesc(String desc) {
        return Strings.isNullOrEmpty(desc) ? "''" : desc;
    }
}
