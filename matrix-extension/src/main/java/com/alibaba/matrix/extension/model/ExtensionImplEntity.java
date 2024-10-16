package com.alibaba.matrix.extension.model;

import java.io.Serializable;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class ExtensionImplEntity implements Serializable {

    private static final long serialVersionUID = -8095928529159883845L;

    /**
     * see {@link ExtensionImplType}
     */
    public final String type;

    public final Object instance;

    public final int priority;

    public final String desc;

    public ExtensionImplEntity(String type, Object instance, int priority, String desc) {
        this.type = type;
        this.instance = instance;
        this.priority = priority;
        this.desc = desc;
    }
}
