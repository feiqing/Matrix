package com.alibaba.matrix.extension;

import com.alibaba.matrix.extension.router.ExtensionRouter;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:31.
 */
public class ExtensionContext {

    private static final String SYS_PREFIX = "__SYS:";

    private static final String BIZ_PREFIX = "__BIZ:";

    private static final String ROUTER = "router__";

    private static final String CODE = "code__";

    // scope -> [ key -> value ]
    private static final ThreadLocal<Map<String, Map<String, Serializable>>> contextMap = ThreadLocal.withInitial(HashMap::new);

    public static boolean isScopeExists(String scope) {
        return contextMap.get().containsKey(scope);
    }

    // code
    protected static void setExtensionCode(String scope, String code) {
        setCtxVal(scope, SYS_PREFIX, CODE, code);
    }

    public static String getExtensionCode(String scope) {
        return getCtxVal(scope, SYS_PREFIX, CODE);
    }

    public static void rmExtensionCode(String scope) {
        rmCtxVal(scope, SYS_PREFIX, CODE);
    }

    // router
    protected static void setExtensionRouter(String scope, ExtensionRouter router) {
        setCtxVal(scope, SYS_PREFIX, ROUTER, router);
    }

    public static ExtensionRouter getExtensionRouter(String scope) {
        return getCtxVal(scope, SYS_PREFIX, ROUTER);
    }

    public static void rmExtensionRouter(String scope) {
        rmCtxVal(scope, SYS_PREFIX, ROUTER);
    }

    // 扩展自定义上下文: 业务可以设置自定义
    public static <Ctx extends Serializable> void addExtCtx(String scope, Ctx ctx) {
        if (ctx != null) {
            setCtxVal(scope, BIZ_PREFIX, ctx.getClass().getName(), ctx);
        }
    }

    public static <Ctx extends Serializable> void addExtCtx(String scope, String key, Ctx ctx) {
        setCtxVal(scope, BIZ_PREFIX, key, ctx);
    }

    public static <Ctx extends Serializable> Ctx getExtCtx(String scope, String key) {
        return getCtxVal(scope, BIZ_PREFIX, key);
    }

    public static <Ctx extends Serializable> Ctx getExtCtx(String scope, Class<Ctx> ctx) {
        return getCtxVal(scope, BIZ_PREFIX, ctx.getName());
    }

    public static void rmExtCtx(String scope, String key) {
        rmCtxVal(scope, BIZ_PREFIX, key);
    }

    public static <Ctx extends Serializable> void rmExtCtx(String scope, Class<Ctx> ctx) {
        rmExtCtx(scope, ctx.getName());
    }

    public static void clear() {
        contextMap.remove();
    }

    public static void clear(String scope) {
        contextMap.get().remove(scope);
    }

    // helper
    private static void setCtxVal(String scope, String prefix, String key, Serializable ctxVal) {
        contextMap.get().computeIfAbsent(scope, _K -> new HashMap<>()).put(prefix + key, ctxVal);
    }

    @SuppressWarnings("unchecked")
    private static <Ctx extends Serializable> Ctx getCtxVal(String scope, String prefix, String key) {
        return (Ctx) contextMap.get().computeIfAbsent(scope, _K -> new HashMap<>()).get(prefix + key);
    }

    private static void rmCtxVal(String scope, String prefix, String key) {
        contextMap.get().getOrDefault(scope, Collections.emptyMap()).remove(prefix + key);
    }

    // ContextCopy(for 多线程/RPC)
    public static Map<String, Map<String, Serializable>> getCopyOfContextMap() {
        Map<String, Map<String, Serializable>> contextMap = ExtensionContext.contextMap.get();

        Map<String, Map<String, Serializable>> copyOfContextMap = new HashMap<>(contextMap.size());
        contextMap.forEach((k, v) -> copyOfContextMap.put(k, new HashMap<>(v)));

        return copyOfContextMap;
    }

    public static void setContextMap(Map<String, Map<String, Serializable>> contextMap) {
        ExtensionContext.contextMap.set(contextMap);
    }
}
