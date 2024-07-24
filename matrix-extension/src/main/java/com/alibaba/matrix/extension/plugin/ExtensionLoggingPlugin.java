package com.alibaba.matrix.extension.plugin;

import com.alibaba.matrix.extension.ExtensionContext;
import org.slf4j.Logger;


/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/15 20:25.
 */
public class ExtensionLoggingPlugin implements ExtensionPlugin {

    @Override
    public Object invoke(ExtensionInvocation invocation) throws Exception {
        long start = System.currentTimeMillis();
        String group = invocation.getGroup();
        String extType = invocation.getExtType().getName();
        String extPoint = invocation.getExtPoint().getName();
        String router = ExtensionContext.getExtensionRouter(group).getClass().getSimpleName();
        String code = ExtensionContext.getExtensionCode(group);
        String type = invocation.getType();
        Object instance = invocation.getInstance();
        Object[] args = invocation.getArgs();
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
                getLogger().info("{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|", group, extType, extPoint, router, code, type, instance, getArgs(args), getResult(result), rt);
            } else {
                getLogger().error("{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|", group, extType, extPoint, router, code, type, instance, getArgs(args), getResult(result), rt, except);
            }
        }
    }

    protected Logger getLogger() {
        return com.alibaba.matrix.extension.utils.Logger.log;
    }

    protected Object getArgs(Object[] args) {
        return "";
    }

    protected Object getResult(Object result) {
        return "";
    }
}
