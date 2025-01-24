package com.alibaba.matrix.extension;

import com.alibaba.matrix.extension.core.ExtensionExecutor;
import com.alibaba.matrix.extension.reducer.Reducer;
import com.alibaba.matrix.extension.reducer.Reducers;

import java.util.Collections;
import java.util.List;
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
     * Invokes a single implementation of the specified extension.
     *
     * @param code      The code identifier of the extension
     * @param extension The extension interface class
     * @param action    The action to be executed
     * @param <Ext>     The type of the extension interface
     * @param <R>       The type of the action result
     * @return The result of the action
     */
    public static <Ext, R> R invoke(String code, Class<Ext> extension, Function<Ext, R> action) {
        return ExtensionExecutor.execute(BASE_SCOPE, Collections.singletonList(code), extension, action, Reducers.first());
    }

    /**
     * Invokes multiple implementations of the specified extension and processes the results using the specified reducer.
     *
     * @param code      The code identifier of the extension
     * @param extension The extension interface class
     * @param action    The action to be executed
     * @param reducer   The reducer for processing results
     * @param <Ext>     The type of the extension interface
     * @param <T>       The type of the intermediate action result
     * @param <R>       The type of the final action result
     * @return The result of the action
     */
    public static <Ext, T, R> R invoke(String code, Class<Ext> extension, Function<Ext, T> action, Reducer<T, R> reducer) {
        return ExtensionExecutor.execute(BASE_SCOPE, Collections.singletonList(code), extension, action, reducer);
    }

    /**
     * Invokes multiple implementations of the specified extension.
     *
     * @param codes     The list of code identifiers of the extensions
     * @param extension The extension interface class
     * @param action    The action to be executed
     * @param <Ext>     The type of the extension interface
     * @param <R>       The type of the action result
     * @return The result of the action
     */
    public static <Ext, R> R invoke(List<String> codes, Class<Ext> extension, Function<Ext, R> action) {
        return ExtensionExecutor.execute(BASE_SCOPE, codes, extension, action, Reducers.first());
    }

    /**
     * Invokes multiple implementations of the specified extension and processes the results using the specified reducer.
     *
     * @param codes     The list of code identifiers of the extensions
     * @param extension The extension interface class
     * @param action    The action to be executed
     * @param reducer   The reducer for processing results
     * @param <Ext>     The type of the extension interface
     * @param <T>       The type of the intermediate action result
     * @param <R>       The type of the final action result
     * @return The result of the action
     */
    public static <Ext, T, R> R invoke(List<String> codes, Class<Ext> extension, Function<Ext, T> action, Reducer<T, R> reducer) {
        return ExtensionExecutor.execute(BASE_SCOPE, codes, extension, action, reducer);
    }

    /**
     * Invokes a single implementation of the specified extension within the given scope.
     *
     * @param scope     The scope of the extension
     * @param code      The code identifier of the extension
     * @param extension The extension interface class
     * @param action    The action to be executed
     * @param <Ext>     The type of the extension interface
     * @param <R>       The type of the action result
     * @return The result of the action
     */
    public static <Ext, R> R invoke(String scope, String code, Class<Ext> extension, Function<Ext, R> action) {
        return ExtensionExecutor.execute(scope, Collections.singletonList(code), extension, action, Reducers.first());
    }

    /**
     * Invokes a single implementation of the specified extension within the given scope and processes the results using the specified reducer.
     *
     * @param scope     The scope of the extension
     * @param code      The code identifier of the extension
     * @param extension The extension interface class
     * @param action    The action to be executed
     * @param reducer   The reducer for processing results
     * @param <Ext>     The type of the extension interface
     * @param <T>       The type of the intermediate action result
     * @param <R>       The type of the final action result
     * @return The result of the action
     */
    public static <Ext, T, R> R invoke(String scope, String code, Class<Ext> extension, Function<Ext, T> action, Reducer<T, R> reducer) {
        return ExtensionExecutor.execute(scope, Collections.singletonList(code), extension, action, reducer);
    }

    /**
     * Invokes multiple implementations of the specified extension within the given scope.
     *
     * @param scope     The scope of the extension
     * @param codes     The list of code identifiers of the extensions
     * @param extension The extension interface class
     * @param action    The action to be executed
     * @param <Ext>     The type of the extension interface
     * @param <R>       The type of the action result
     * @return The result of the action
     */
    public static <Ext, R> R invoke(String scope, List<String> codes, Class<Ext> extension, Function<Ext, R> action) {
        return ExtensionExecutor.execute(scope, codes, extension, action, Reducers.first());
    }

    /**
     * Invokes multiple implementations of the specified extension within the given scope and processes the results using the specified reducer.
     *
     * @param scope     The scope of the extension
     * @param codes     The list of code identifiers of the extensions
     * @param extension The extension interface class
     * @param action    The action to be executed
     * @param reducer   The reducer for processing results
     * @param <Ext>     The type of the extension interface
     * @param <T>       The type of the intermediate action result
     * @param <R>       The type of the final action result
     * @return The result of the action
     */
    public static <Ext, T, R> R invoke(String scope, List<String> codes, Class<Ext> extension, Function<Ext, T> action, Reducer<T, R> reducer) {
        return ExtensionExecutor.execute(scope, codes, extension, action, reducer);
    }
}
