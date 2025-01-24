package com.alibaba.matrix.extension.plugin;

import com.alibaba.matrix.extension.reducer.Reducer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;

import static com.alibaba.matrix.extension.util.Logger.getDesc;

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
        List<String> codes = invocation.getCodes();
        String extension = invocation.getExtension().getSimpleName();

        Function action = invocation.getAction();
        Reducer reducer = invocation.getReducer();

        String code = invocation.getCode();
        String type = invocation.getType();
        int priority = invocation.getPriority();
        String desc = invocation.getDesc();
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
                logger.info("{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|", scope, StringUtils.join(codes, ','), extension, code, type, priority, getDesc(desc), instance.toString(), getResult(result), rt);
            } else {
                logger.error("{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|", scope, StringUtils.join(codes, ','), extension, code, type, priority, getDesc(desc), instance.toString(), getResult(result), rt, except);
            }
        }
    }

    protected Object getResult(Object result) {
        return "";
    }
}
