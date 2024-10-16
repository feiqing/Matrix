package com.alibaba.matrix.extension.model.config;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class Provider implements Serializable {

    private static final long serialVersionUID = 6240831461699996545L;

    public String clazz;

    public String method;

    public String arg0;

    public Provider(String clazz, String method) {
        Preconditions.checkArgument(!StringUtils.isAnyBlank(clazz, method));
        this.clazz = clazz;
        this.method = method;
    }
}
