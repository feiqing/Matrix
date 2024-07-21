package com.alibaba.matrix.extension.model;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/7/23 10:42.
 */
public class ExtImpl implements Serializable {

    private static final long serialVersionUID = -8095928529159883845L;

    /**
     * {@link ExtImplType}
     */
    public final String type;

    public final Object instance;

    public ExtImpl(@Nonnull String type, @Nonnull Object instance) {
        this.type = type;
        this.instance = instance;
    }
}
