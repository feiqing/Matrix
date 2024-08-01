package com.alibaba.matrix.extension;

import com.alibaba.matrix.extension.router.BaseExtensionRouter;
import com.alibaba.matrix.extension.core.ExtensionExecutor;
import com.alibaba.matrix.extension.reducer.Reducer;
import com.alibaba.matrix.extension.reducer.Reducers;
import com.alibaba.matrix.extension.router.ExtensionRouter;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.function.Function;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:31.
 */
public final class ExtensionInvoker {

    public static final String BASE_SCOPE = "BASE";

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
        return invoke(BASE_SCOPE, ext, action, Reducers.firstOf());
    }

    public static <Ext, T, R> R invoke(Class<Ext> ext, Function<Ext, T> action, Reducer<T, R> reducer) {
        return invoke(BASE_SCOPE, ext, action, reducer);
    }

    /**
     * 扩展执行: 携带领域信息(scope)的扩展执行
     *
     * @param scope
     * @param ext
     * @param action
     * @param <Ext>
     * @param <R>
     * @return
     */
    public static <Ext, R> R invoke(String scope, Class<Ext> ext, Function<Ext, R> action) {
        return invoke(scope, ext, action, Reducers.firstOf());
    }

    public static <Ext, T, R> R invoke(String scope, Class<Ext> ext, Function<Ext, T> action, Reducer<T, R> reducer) {
        return ExtensionExecutor.execute(scope, ext, action, reducer);
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
    public static <Ext, T, R> R _invoke_(String scope, String code, Class<Ext> ext, Function<Ext, T> action, Reducer<T, R> reducer) {
        resolve(scope, BASE_EXTENSION_ROUTER, Function.identity(), code);
        try {
            return invoke(ext, action, reducer);
        } finally {
            ExtensionContext.rmExtensionRouter(scope);
            ExtensionContext.rmExtensionCode(scope);
        }
    }

    public static <T> String resolve(Function<T, String> codeGenerator, T t) {
        return resolve(BASE_SCOPE, BASE_EXTENSION_ROUTER, codeGenerator, t);
    }

    public static <T> String resolve(String scope, Function<T, String> codeGenerator, T t) {
        return resolve(scope, BASE_EXTENSION_ROUTER, codeGenerator, t);
    }

    public static <T> String resolve(String scope, ExtensionRouter router, Function<T, String> codeGenerator, T t) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(scope));
        Preconditions.checkState(!ExtensionContext.isScopeExists(scope));   // 不支持同一个分组重复设置扩展身份
        Preconditions.checkArgument(router != null);
        Preconditions.checkArgument(codeGenerator != null);

        String code = codeGenerator.apply(t);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(code));
        ExtensionContext.setExtensionCode(scope, code);
        ExtensionContext.setExtensionRouter(scope, router);
        return code;
    }

    public static void clear(String scope) {
        ExtensionContext.clear(scope);
    }

    public static void clear() {
        ExtensionContext.clear();
    }
}
