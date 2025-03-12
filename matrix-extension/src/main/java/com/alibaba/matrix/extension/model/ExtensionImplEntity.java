package com.alibaba.matrix.extension.model;

import java.io.Serializable;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class ExtensionImplEntity implements Serializable {

    private static final long serialVersionUID = -8095928529159883845L;

    public final String namespace;

    public final String code;

    /**
     * see {@link ExtensionImplType}
     */
    public final String type;

    public final int priority;

    public final String desc;

    public final Object instance;

    public ExtensionImplEntity(String namespace, String code, String type, int priority, String desc, Object instance) {
        this.namespace = namespace;
        this.code = code;
        this.type = type;
        this.priority = priority;
        this.desc = desc;
        this.instance = instance;
    }
}
