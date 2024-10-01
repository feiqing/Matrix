package com.alibaba.matrix.extension.model;

import javax.annotation.Nonnull;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/7/23 22:41.
 */
public class Impl {

    public final String scope;

    public final String code;

    public final String type;

    public final int priority;

    public final String desc;

    public volatile Object instance = null;

    public ObjectT object = null;

    public Bean bean = null;

    public Hsf hsf = null;

    public Dubbo dubbo = null;
    
    public Http http = null;

    public Groovy groovy = null;

    public SpEL spel = null;

    public boolean isLazy() {
        if (instance != null) {
            return false;
        }
        if (object != null) {
            return object.lazy;
        }
        if (bean != null) {
            return bean.lazy;
        }
        if (hsf != null) {
            return hsf.lazy;
        }
        if (dubbo != null) {
            return dubbo.lazy;
        }
        if (http != null) {
            return http.lazy;
        }
        if (groovy != null) {
            return groovy.lazy;
        }
        if (spel != null) {
            return spel.lazy;
        }
        return false;
    }

    public Impl(@Nonnull String scope, @Nonnull String code, @Nonnull String type, @Nonnull int priority, String desc) {
        this.scope = scope;
        this.code = code;
        this.type = type;
        this.priority = priority;
        this.desc = desc;
    }
}