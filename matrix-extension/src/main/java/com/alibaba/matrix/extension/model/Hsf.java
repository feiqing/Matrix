package com.alibaba.matrix.extension.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.Serializable;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2021/3/28 22:42.
 */
public class Hsf implements Serializable {

    private static final long serialVersionUID = -6757315625134300013L;

    public String service;

    public String version;

    public String group = "HSF";

    public Integer timeout;

    public boolean lazy = false;

    public Hsf(String service, String version) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(service));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(version));
        this.service = service;
        this.version = version;
    }
}
