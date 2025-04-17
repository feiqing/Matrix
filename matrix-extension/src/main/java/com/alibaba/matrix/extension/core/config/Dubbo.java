package com.alibaba.matrix.extension.core.config;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.Serializable;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class Dubbo implements Serializable {

    private static final long serialVersionUID = -6757315625134300013L;

    public String version;

    public String group;

    public boolean check = true;

    public Integer timeout;

    public String filter;

    public String applicationName;

    public String registryAddress;

    public Dubbo(String version, String group) {
        Preconditions.checkArgument(!(Strings.isNullOrEmpty(version) && Strings.isNullOrEmpty(group)));
        this.version = version;
        this.group = group;
    }
}
