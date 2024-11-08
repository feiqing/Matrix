package com.alibaba.matrix.extension.model.config;

import com.alibaba.matrix.extension.model.ExtensionImplEntity;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class ExtensionScope {

    public final String scope;

    public final Map<String, List<ExtensionImpl>> code2impls;

    public final ConcurrentMap<String, List<ExtensionImplEntity>> CODE_TO_EXT_IMPLS_CACHE = new ConcurrentHashMap<>();

    public ExtensionScope(@Nonnull String scope, @Nonnull Map<String, List<ExtensionImpl>> code2impls) {
        this.scope = scope;
        this.code2impls = code2impls;
    }
}
