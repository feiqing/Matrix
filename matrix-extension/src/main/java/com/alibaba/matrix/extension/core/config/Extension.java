package com.alibaba.matrix.extension.core.config;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class Extension {

    public final Class<?> clazz;

    public final String desc;

    public final Object base;

    public final Map<String, ExtensionNamespace> namespaceMap;

    public Extension(@Nonnull Class<?> clazz, String desc, @Nonnull Object base, @Nonnull Map<String, ExtensionNamespace> namespaceMap) {
        this.clazz = clazz;
        this.desc = desc;
        this.base = base;
        this.namespaceMap = namespaceMap;
    }
}
