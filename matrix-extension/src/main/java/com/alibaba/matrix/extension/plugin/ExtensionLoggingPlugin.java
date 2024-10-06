package com.alibaba.matrix.extension.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/12 11:20.
 */
public class ExtensionLoggingPlugin implements ExtensionPlugin {

    private static final Logger logger = LoggerFactory.getLogger("EXTENSION");

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
                logger.info("{}|{}|{}|{}|{}|{}|{}|", scope, code, ext, type, instance, getResult(result), rt);
            } else {
                logger.error("{}|{}|{}|{}|{}|{}|{}|", scope, code, ext, type, instance, getResult(result), rt, except);
            }
        }
    }

    protected Object getResult(Object result) {
        return "";
    }
}
