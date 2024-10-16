package com.alibaba.matrix.extension.model.config;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class ExtensionImpl implements Serializable {

    private static final long serialVersionUID = 7336164124598769112L;

    public final String scope;

    public final String code;

    public final String type;

    public final int priority;

    public final boolean lazy;

    public final String desc;

    public volatile Object instance = null;

    // ----
    public ObjectT object = null;

    public Bean bean = null;

    public Guice guice = null;

    public Hsf hsf = null;

    public Dubbo dubbo = null;

    public Http http = null;

    public Groovy groovy = null;

    public SpEL spel = null;

    public Provider provider = null;

    public ExtensionImpl(@Nonnull String scope, @Nonnull String code, @Nonnull String type, @Nonnull int priority, @Nonnull boolean lazy, String desc) {
        this.scope = scope;
        this.code = code;
        this.type = type;
        this.priority = priority;
        this.lazy = lazy;
        this.desc = desc;
    }
}