package com.alibaba.matrix.extension;

import com.alibaba.matrix.extension.core.BaseExtensionRouter;
import com.alibaba.matrix.extension.core.ExtensionExecutor;
import com.alibaba.matrix.extension.reducer.Reducer;
import com.alibaba.matrix.extension.reducer.Reducers;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.function.Function;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:31.
 */
public final class ExtensionInvoker {

    public static final String BASE_GROUP = "BASE";

    public static final ExtensionRouter BASE_EXTENSION_ROUTER = new BaseExtensionRouter();

    private ExtensionInvoker() {
    }

    /**
     * 扩展执行
     *
     * @param ext
     * @param action
     * @param <Ext>
     * @param <R>
     * @return
     */
    public static <Ext, R> R invoke(Class<Ext> ext, Function<Ext, R> action) {
        return invoke(BASE_GROUP, ext, action, Reducers.firstOf());
    }

    public static <Ext, T, R> R invoke(Class<Ext> ext, Function<Ext, T> action, Reducer<T, R> reducer) {
        return invoke(BASE_GROUP, ext, action, reducer);
    }

    /**
     * 扩展执行: 携带分组信息(group)的扩展执行
     *
     * @param group
     * @param ext
     * @param action
     * @param <Ext>
     * @param <R>
     * @return
     */
    public static <Ext, R> R invoke(String group, Class<Ext> ext, Function<Ext, R> action) {
        return invoke(group, ext, action, Reducers.firstOf());
    }

    public static <Ext, T, R> R invoke(String group, Class<Ext> ext, Function<Ext, T> action, Reducer<T, R> reducer) {
        return ExtensionExecutor.execute(group, ext, action, reducer);
    }

    /**
     * 扩展执行: 执行一次(仅用于单测场景, 务必不要应用于正式业务逻辑中, 否则后果自负!)
     *
     * @param code
     * @param ext
     * @param action
     * @param reducer
     * @param <Ext>
     * @param <T>
     * @param <R>
     * @return
     */
    public static <Ext, T, R> R _invoke_(String group, String code, Class<Ext> ext, Function<Ext, T> action, Reducer<T, R> reducer) {
        resolve(group, BASE_EXTENSION_ROUTER, Function.identity(), code);
        try {
            return invoke(ext, action, reducer);
        } finally {
            ExtensionContext.rmExtensionRouter(group);
            ExtensionContext.rmExtensionCode(group);
        }
    }

    public static <T> String resolve(Function<T, String> codeResolver, T t) {
        return resolve(BASE_GROUP, codeResolver, t);
    }

    public static <T> String resolve(String group, Function<T, String> codeResolver, T t) {
        return resolve(group, BASE_EXTENSION_ROUTER, codeResolver, t);
    }

    public static <T> String resolve(String group, ExtensionRouter router, Function<T, String> codeResolver, T t) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(group));
        Preconditions.checkState(!ExtensionContext.isGroupExists(group));   // 不支持同一个分组重复设置扩展身份
        Preconditions.checkArgument(router != null);
        Preconditions.checkArgument(codeResolver != null);

        String code = codeResolver.apply(t);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(code));
        ExtensionContext.setExtensionCode(group, code);
        ExtensionContext.setExtensionRouter(group, router);
        return code;
    }

    public static void clear(String group) {
        ExtensionContext.clear(group);
    }

    public static void clear() {
        ExtensionContext.clear();
    }
}
