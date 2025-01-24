package com.alibaba.matrix.extension;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.collections4.MapUtils;

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

    private static final TransmittableThreadLocal<Map<String, Serializable>> contextMap = new TransmittableThreadLocal<>();

    public static <Ctx extends Serializable> void addExtCtx(Ctx ctx) {
        if (ctx != null) {
            setCtxVal(ctx.getClass().toString(), ctx);
        }
    }

    public static <Ctx extends Serializable> void addExtCtx(String key, Ctx ctx) {
        setCtxVal(key, ctx);
    }

    public static <Ctx extends Serializable> Ctx getExtCtx(String key) {
        return getCtxVal(key);
    }

    public static <Ctx extends Serializable> Ctx getExtCtx(Class<Ctx> ctx) {
        return getCtxVal(ctx.toString());
    }

    public static void rmExtCtx(String key) {
        rmCtxVal(key);
    }

    public static <Ctx extends Serializable> void rmExtCtx(Class<Ctx> ctx) {
        rmExtCtx(ctx.getName());
    }

    public static void clear() {
        contextMap.remove();
    }

    // helper
    private static void setCtxVal(String key, Serializable ctxVal) {
        Map<String, Serializable> map = contextMap.get();
        if (map == null) {
            map = new HashMap<>();
            contextMap.set(map);
        }
        map.put(key, ctxVal);
    }

    @SuppressWarnings("unchecked")
    private static <Ctx extends Serializable> Ctx getCtxVal(String key) {
        Map<String, Serializable> map = contextMap.get();
        if (MapUtils.isEmpty(map)) {
            return null;
        }
        return (Ctx) map.get(key);
    }

    private static void rmCtxVal(String key) {
        Map<String, Serializable> map = contextMap.get();
        if (MapUtils.isEmpty(map)) {
            return;
        }
        map.remove(key);
        if (map.isEmpty()) {
            contextMap.remove();
        }
    }

    // ContextCopy(for Multi Thread/RPC)
    public static Map<String, Serializable> getCopyOfContextMap() {
        return Collections.unmodifiableMap(ExtensionContext.contextMap.get());
    }

    public static void setContextMap(Map<String, Serializable> contextMap) {
        ExtensionContext.contextMap.set(new HashMap<>(contextMap));
    }
}
