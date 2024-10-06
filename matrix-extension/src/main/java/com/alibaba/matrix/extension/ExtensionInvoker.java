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
     * @param ext
     * @param action
     * @param <Ext>
     * @param <R>
     * @return
     */
    public static <Ext, R> R invoke(String code, Class<Ext> ext, Function<Ext, R> action) {
        return ExtensionExecutor.execute(BASE_SCOPE, code, ext, action, Reducers.first());
    }

    public static <Ext, T, R> R invoke(String code, Class<Ext> ext, Function<Ext, T> action, Reducer<T, R> reducer) {
        return ExtensionExecutor.execute(BASE_SCOPE, code, ext, action, reducer);
    }

    /**
     * 扩展执行: 携带领域信息(scope)的扩展执行
     *
     * @param scope
     * @param code
     * @param ext
     * @param action
     * @param <Ext>
     * @param <R>
     * @return
     */
    public static <Ext, R> R invoke(String scope, String code, Class<Ext> ext, Function<Ext, R> action) {
        return ExtensionExecutor.execute(scope, code, ext, action, Reducers.first());
    }

    public static <Ext, T, R> R invoke(String scope, String code, Class<Ext> ext, Function<Ext, T> action, Reducer<T, R> reducer) {
        return ExtensionExecutor.execute(scope, code, ext, action, reducer);
    }
}
