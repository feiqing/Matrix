package com.alibaba.matrix.extension.core;

import com.alibaba.matrix.extension.ExtensionContext;
import com.alibaba.matrix.extension.router.ExtensionRouter;
import com.alibaba.matrix.extension.exception.ExtensionRuntimeException;
import com.alibaba.matrix.extension.model.ExtImpl;
import com.alibaba.matrix.extension.plugin.ExtensionInvocation;
import com.alibaba.matrix.extension.proxy.ProxyFactory;
import com.alibaba.matrix.extension.reducer.Reducer;
import com.alibaba.matrix.extension.utils.Namespaces;
import com.google.common.base.Preconditions;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import static com.alibaba.matrix.extension.core.ExtensionContainer.plugins;


/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 17:47.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ExtensionExecutor {

    private static final ConcurrentMap<String, Object> extProxies = new ConcurrentHashMap<>();

    public static <Ext, T, R> R execute(String scope, Class<Ext> ext, Function<Ext, T> action, Reducer<T, R> reducer) {
        Preconditions.checkState(ExtensionContext.isScopeExists(scope));
        try {
            Wrapper wrapper = new Wrapper();
            wrapper.reducer = reducer;
            ExtensionContext.addExtCtx(scope, wrapper);

            Object result = action.apply((Ext) extProxies.computeIfAbsent(scope + ":" + ext, _K -> ProxyFactory.newProxy(scope, ext)));
            if (reducer.sameType()) {
                return (R) result;
            } else {
                return (R) wrapper.result;
            }
        } finally {
            ExtensionContext.rmExtCtx(scope, Wrapper.class);
        }
    }

    public static <Ext> Object callback(String scope, Class<Ext> ext, Method method, Object[] args, Reducer reducer) throws Throwable {
        try {
            ExtensionContext.addExtCtx(scope, Namespaces.EXT_TYPE, ext);
            ExtensionContext.addExtCtx(scope, Namespaces.EXT_POINT, method.getName());
            reducer = reducer != null ? reducer : ExtensionContext.getExtCtx(scope, Wrapper.class).reducer;

            List<ExtImpl> impls = getExtensionImpls(scope, ext, args);
            List<Object> results = new ArrayList<>(impls.size());

            for (ExtImpl impl : impls) {
                Preconditions.checkState(ext.isInstance(impl.instance));
                Object result = execute(scope, ext, impl, method, args);
                results.add(result);
                if (reducer.willBreak(result)) {
                    break;
                }
            }

            Object result = reducer.reduce(results);
            if (reducer.sameType()) {
                return result;
            } else {
                ExtensionContext.getExtCtx(scope, Wrapper.class).result = result;
                return null;
            }
        } finally {
            ExtensionContext.rmExtCtx(scope, Namespaces.EXT_POINT);
            ExtensionContext.rmExtCtx(scope, Namespaces.EXT_TYPE);
        }
    }

    private static <Ext> Object execute(String scope, Class<Ext> ext, ExtImpl impl, Method method, Object[] args) throws Exception {
        Preconditions.checkState(ext.isInstance(impl.instance));
        return new ExtensionInvocation(scope, ext, method, impl.type, impl.instance, args, plugins).proceed();
    }

    private static List<ExtImpl> getExtensionImpls(String scope, Class<?> ext, Object... args) {
        ExtensionRouter router = ExtensionContext.getExtensionRouter(scope);
        Preconditions.checkState(router != null);
        List<ExtImpl> impls = router.route(ext, scope, args);
        if (CollectionUtils.isEmpty(impls)) {
            throw new ExtensionRuntimeException(String.format("[ExtensionRouter:%s] could not found any impls for [Extension:%s]!", router, ext.getName()));
        }
        return impls;
    }

    @Data
    public static class Wrapper implements Serializable {

        private static final long serialVersionUID = -1287460727677367662L;

        Reducer<?, ?> reducer;

        Object result;
    }
}
