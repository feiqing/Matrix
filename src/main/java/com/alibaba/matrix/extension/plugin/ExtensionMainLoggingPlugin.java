package com.alibaba.matrix.extension.plugin;

import com.alibaba.matrix.extension.ExtensionContext;
import com.alibaba.matrix.extension.utils.Logger;

import static com.alibaba.matrix.logging.MatrixLogging.extensionLogger;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/15 20:25.
 */
public class ExtensionMainLoggingPlugin implements ExtensionPlugin {

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
                Logger.log.info("{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|", group, extType, extPoint, router, code, type, instance, getArgs(args), getResult(result), rt);
            } else {
                Logger.log.error("{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|", group, extType, extPoint, router, code, type, instance, getArgs(args), getResult(result), rt, except);
            }
        }
    }

    protected Object getArgs(Object[] args) {
        return "";
    }

    protected Object getResult(Object result) {
        return "";
    }
}
