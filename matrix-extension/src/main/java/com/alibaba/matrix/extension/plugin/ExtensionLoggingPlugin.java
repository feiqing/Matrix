package com.alibaba.matrix.extension.plugin;

import org.slf4j.Logger;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/15 20:25.
 */
public class ExtensionLoggingPlugin implements ExtensionPlugin {

    @Override
    public Object invoke(ExtensionInvocation invocation) {
        long start = System.currentTimeMillis();
        String scope = invocation.getScope();
        String code = invocation.getCode();
        String ext = invocation.getExt().getName();
        String type = invocation.getType();
        Object instance = invocation.getInstance();
        Object result = null;
        Throwable except = null;
        try {
            return (result = invocation.proceed());
        } catch (Throwable t) {
            except = t;
            throw t;
        } finally {
            long rt = System.currentTimeMillis() - start;
            if (except == null) {
                getLogger().info("{}|{}|{}|{}|{}|{}|{}|", scope, code, ext, type, instance, getResult(result), rt);
            } else {
                getLogger().error("{}|{}|{}|{}|{}|{}|{}|", scope, code, ext, type, instance, getResult(result), rt, except);
            }
        }
    }

    protected Logger getLogger() {
        return com.alibaba.matrix.extension.util.Logger.log;
    }

    protected Object getResult(Object result) {
        return "";
    }
}
