package com.alibaba.matrix.extension.model.config;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.Serializable;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class Hsf implements Serializable {

    private static final long serialVersionUID = -6757315625134300013L;

    public String service;

    public String version;

    public String group = "HSF";

    public Integer timeout;

    public Hsf(String service, String version) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(service));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(version));
        this.service = service;
        this.version = version;
    }
}
