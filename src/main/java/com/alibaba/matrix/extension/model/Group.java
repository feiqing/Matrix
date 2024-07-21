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
public class Group {

    public final String group;

    public final Map<String, List<Impl>> code2impls;

    public final ConcurrentMap<String, List<ExtImpl>> CODE_2_IMPLS_CACHE = new ConcurrentHashMap<>();

    public Group(@Nonnull String group, @Nonnull Map<String, List<Impl>> code2impls) {
        this.group = group;
        this.code2impls = code2impls;
    }
}
