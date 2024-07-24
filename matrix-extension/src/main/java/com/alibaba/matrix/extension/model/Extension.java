package com.alibaba.matrix.extension.model;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/7/23 22:40.
 */
public class Extension {

    public final Class<?> clazz;

    // 默认实现是一定不支持懒加载的
    public final Object base;

    public final Map<String, Group> groupMap;

    public Extension(@Nonnull Class<?> clazz, @Nonnull Object base, @Nonnull Map<String, Group> groupMap) {
        this.clazz = clazz;
        this.base = base;
        this.groupMap = groupMap;
    }
}
