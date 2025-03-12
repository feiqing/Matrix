package com.alibaba.matrix.extension;

import com.alibaba.matrix.extension.core.ExtensionExecutor;
import com.alibaba.matrix.extension.reducer.Reducer;
import com.alibaba.matrix.extension.reducer.Reducers;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 2.0
 * @since 2022/3/30 10:31.
 */
public final class ExtensionInvoker {

    public static final String DEFAULT_NAMESPACE = "BASE";

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
        return ExtensionExecutor.execute(DEFAULT_NAMESPACE, Collections.singletonList(code), extension, action, Reducers.first());
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
        return ExtensionExecutor.execute(DEFAULT_NAMESPACE, Collections.singletonList(code), extension, action, reducer);
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
        return ExtensionExecutor.execute(DEFAULT_NAMESPACE, codes, extension, action, Reducers.first());
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
        return ExtensionExecutor.execute(DEFAULT_NAMESPACE, codes, extension, action, reducer);
    }

    /**
     * Invokes a single implementation of the specified extension within the given namespace.
     *
     * @param namespace The namespace of the extension
     * @param code      The code identifier of the extension
     * @param extension The extension interface class
     * @param action    The action to be executed
     * @param <Ext>     The type of the extension interface
     * @param <R>       The type of the action result
     * @return The result of the action
     */
    public static <Ext, R> R invoke(String namespace, String code, Class<Ext> extension, Function<Ext, R> action) {
        return ExtensionExecutor.execute(namespace, Collections.singletonList(code), extension, action, Reducers.first());
    }

    /**
     * Invokes a single implementation of the specified extension within the given namespace and processes the results using the specified reducer.
     *
     * @param namespace The namespace of the extension
     * @param code      The code identifier of the extension
     * @param extension The extension interface class
     * @param action    The action to be executed
     * @param reducer   The reducer for processing results
     * @param <Ext>     The type of the extension interface
     * @param <T>       The type of the intermediate action result
     * @param <R>       The type of the final action result
     * @return The result of the action
     */
    public static <Ext, T, R> R invoke(String namespace, String code, Class<Ext> extension, Function<Ext, T> action, Reducer<T, R> reducer) {
        return ExtensionExecutor.execute(namespace, Collections.singletonList(code), extension, action, reducer);
    }

    /**
     * Invokes multiple implementations of the specified extension within the given namespace.
     *
     * @param namespace The namespace of the extension
     * @param codes     The list of code identifiers of the extensions
     * @param extension The extension interface class
     * @param action    The action to be executed
     * @param <Ext>     The type of the extension interface
     * @param <R>       The type of the action result
     * @return The result of the action
     */
    public static <Ext, R> R invoke(String namespace, List<String> codes, Class<Ext> extension, Function<Ext, R> action) {
        return ExtensionExecutor.execute(namespace, codes, extension, action, Reducers.first());
    }

    /**
     * Invokes multiple implementations of the specified extension within the given namespace and processes the results using the specified reducer.
     *
     * @param namespace The namespace of the extension
     * @param codes     The list of code identifiers of the extensions
     * @param extension The extension interface class
     * @param action    The action to be executed
     * @param reducer   The reducer for processing results
     * @param <Ext>     The type of the extension interface
     * @param <T>       The type of the intermediate action result
     * @param <R>       The type of the final action result
     * @return The result of the action
     */
    public static <Ext, T, R> R invoke(String namespace, List<String> codes, Class<Ext> extension, Function<Ext, T> action, Reducer<T, R> reducer) {
        return ExtensionExecutor.execute(namespace, codes, extension, action, reducer);
    }
}
