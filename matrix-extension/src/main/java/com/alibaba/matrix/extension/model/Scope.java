package com.alibaba.matrix.extension.model;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/7/23 22:40.
 */
public class Scope {

    public final String scope;

    public final Map<String, List<Impl>> code2impls;

    public final ConcurrentMap<String, List<ExtImpl>> CODE_2_IMPLS_CACHE = new ConcurrentHashMap<>();

    public Scope(@Nonnull String scope, @Nonnull Map<String, List<Impl>> code2impls) {
        this.scope = scope;
        this.code2impls = code2impls;
    }
}
