package com.alibaba.matrix.extension.core.config;

import com.alibaba.matrix.extension.core.ExtensionImplEntity;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class ExtensionNamespace {

    public final String namespace;

    public final Map<String, List<ExtensionImpl>> code2impls;

    public final ConcurrentMap<String, List<ExtensionImplEntity>> CODE_TO_EXT_IMPLS_CACHE = new ConcurrentHashMap<>();

    public ExtensionNamespace(@Nonnull String namespace, @Nonnull Map<String, List<ExtensionImpl>> code2impls) {
        this.namespace = namespace;
        this.code2impls = code2impls;
    }
}
