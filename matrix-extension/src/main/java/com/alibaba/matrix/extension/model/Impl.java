package com.alibaba.matrix.extension.model;

import com.alibaba.matrix.extension.exception.ExtensionException;

import javax.annotation.Nonnull;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/7/23 22:41.
 */
public class Impl {

    public final String group;

    public final String code;

    public final String type;

    public final int priority;

    public String desc;

    public volatile Object instance = null;

    public Bean bean = null;

    public Hsf hsf = null;

    public Dubbo dubbo = null;

    public boolean isLazy() {
        if (instance != null) {
            return false;
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
        // todo msg
        throw new ExtensionException("todo");
    }

    public Impl(@Nonnull String group, @Nonnull String code, @Nonnull String type, @Nonnull int priority) {
        this.group = group;
        this.code = code;
        this.type = type;
        this.priority = priority;
    }
}