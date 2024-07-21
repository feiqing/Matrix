package com.alibaba.matrix.extension.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.Serializable;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/7/23 22:42.
 */
public class Dubbo implements Serializable {

    private static final long serialVersionUID = -6757315625134300013L;

    public String version;

    public String group;

    public Integer timeout;

    public Boolean check;

    public String filter;

    public boolean lazy = false;

    public Dubbo(String version, String group) {
        Preconditions.checkArgument(!(Strings.isNullOrEmpty(version) && Strings.isNullOrEmpty(group)));
        this.version = version;
        this.group = group;
    }
}
