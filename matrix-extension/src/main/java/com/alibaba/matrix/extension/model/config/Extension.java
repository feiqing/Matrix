package com.alibaba.matrix.extension.model.config;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class Extension {

    public final Class<?> clazz;

    public final String desc;

    public final Object base;

    public final Map<String, ExtensionScope> scopeMap;

    public Extension(@Nonnull Class<?> clazz, String desc, @Nonnull Object base, @Nonnull Map<String, ExtensionScope> scopeMap) {
        this.clazz = clazz;
        this.desc = desc;
        this.base = base;
        this.scopeMap = scopeMap;
    }
}
