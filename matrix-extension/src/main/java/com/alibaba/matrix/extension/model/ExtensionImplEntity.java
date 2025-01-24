package com.alibaba.matrix.extension.model;

import java.io.Serializable;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class ExtensionImplEntity implements Serializable {

    private static final long serialVersionUID = -8095928529159883845L;

    public final String scope;

    public final String code;

    /**
     * see {@link ExtensionImplType}
     */
    public final String type;

    public final int priority;

    public final String desc;

    public final Object instance;

    public ExtensionImplEntity(String scope, String code, String type, int priority, String desc, Object instance) {
        this.scope = scope;
        this.code = code;
        this.type = type;
        this.priority = priority;
        this.desc = desc;
        this.instance = instance;
    }
}
