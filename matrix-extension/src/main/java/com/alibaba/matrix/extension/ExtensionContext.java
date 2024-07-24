package com.alibaba.matrix.extension;

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

    // group -> [ key -> value ]
    private static final ThreadLocal<Map<String, Map<String, Serializable>>> contextMap = ThreadLocal.withInitial(HashMap::new);

    public static boolean isGroupExists(String group) {
        return contextMap.get().containsKey(group);
    }

    // code
    protected static void setExtensionCode(String group, String code) {
        setCtxVal(group, SYS_PREFIX, CODE, code);
    }

    public static String getExtensionCode(String group) {
        return getCtxVal(group, SYS_PREFIX, CODE);
    }

    public static void rmExtensionCode(String group) {
        rmCtxVal(group, SYS_PREFIX, CODE);
    }

    // router
    protected static void setExtensionRouter(String group, ExtensionRouter router) {
        setCtxVal(group, SYS_PREFIX, ROUTER, router);
    }

    public static ExtensionRouter getExtensionRouter(String group) {
        return getCtxVal(group, SYS_PREFIX, ROUTER);
    }

    public static void rmExtensionRouter(String group) {
        rmCtxVal(group, SYS_PREFIX, ROUTER);
    }

    // 扩展自定义上下文: 业务可以设置自定义
    public static <Ctx extends Serializable> void addExtCtx(String group, Ctx ctx) {
        if (ctx != null) {
            setCtxVal(group, BIZ_PREFIX, ctx.getClass().getName(), ctx);
        }
    }

    public static <Ctx extends Serializable> void addExtCtx(String group, String key, Ctx ctx) {
        setCtxVal(group, BIZ_PREFIX, key, ctx);
    }

    public static <Ctx extends Serializable> Ctx getExtCtx(String group, String key) {
        return getCtxVal(group, BIZ_PREFIX, key);
    }

    public static <Ctx extends Serializable> Ctx getExtCtx(String group, Class<Ctx> ctx) {
        return getCtxVal(group, BIZ_PREFIX, ctx.getName());
    }

    public static void rmExtCtx(String group, String key) {
        rmCtxVal(group, BIZ_PREFIX, key);
    }

    public static <Ctx extends Serializable> void rmExtCtx(String group, Class<Ctx> ctx) {
        rmExtCtx(group, ctx.getName());
    }

    public static void clear() {
        contextMap.remove();
    }

    public static void clear(String group) {
        contextMap.get().remove(group);
    }

    // helper
    private static void setCtxVal(String group, String prefix, String key, Serializable ctxVal) {
        contextMap.get().computeIfAbsent(group, _K -> new HashMap<>()).put(prefix + key, ctxVal);
    }

    @SuppressWarnings("unchecked")
    private static <Ctx extends Serializable> Ctx getCtxVal(String group, String prefix, String key) {
        return (Ctx) contextMap.get().computeIfAbsent(group, _K -> new HashMap<>()).get(prefix + key);
    }

    private static void rmCtxVal(String group, String prefix, String key) {
        contextMap.get().getOrDefault(group, Collections.emptyMap()).remove(prefix + key);
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
