package com.alibaba.matrix.extension;

import com.alibaba.matrix.extension.core.ExtensionExecutor;
import com.alibaba.matrix.extension.reducer.Reducer;
import com.alibaba.matrix.extension.reducer.Reducers;

import java.util.function.Function;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 2.0
 * @since 2022/3/30 10:31.
 */
public final class ExtensionInvoker {

    public static final String BASE_SCOPE = "BASE";

    private ExtensionInvoker() {
    }

    /**
     * 扩展执行
     *
     * @param code
     * @param extension
     * @param action
     * @param <Ext>
     * @param <R>
     * @return
     */
    public static <Ext, R> R invoke(String code, Class<Ext> extension, Function<Ext, R> action) {
        return ExtensionExecutor.execute(BASE_SCOPE, code, extension, action, Reducers.first());
    }

    public static <Ext, T, R> R invoke(String code, Class<Ext> extension, Function<Ext, T> action, Reducer<T, R> reducer) {
        return ExtensionExecutor.execute(BASE_SCOPE, code, extension, action, reducer);
    }

    /**
     * 扩展执行: 携带领域信息(scope)扩展执行
     *
     * @param scope
     * @param code
     * @param extension
     * @param action
     * @param <Ext>
     * @param <R>
     * @return
     */
    public static <Ext, R> R invoke(String scope, String code, Class<Ext> extension, Function<Ext, R> action) {
        return ExtensionExecutor.execute(scope, code, extension, action, Reducers.first());
    }

    public static <Ext, T, R> R invoke(String scope, String code, Class<Ext> extension, Function<Ext, T> action, Reducer<T, R> reducer) {
        return ExtensionExecutor.execute(scope, code, extension, action, reducer);
    }
}
