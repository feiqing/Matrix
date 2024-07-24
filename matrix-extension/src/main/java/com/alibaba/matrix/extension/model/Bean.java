package com.alibaba.matrix.extension.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/7/23 22:41.
 */
public class Bean implements Serializable {

    private static final long serialVersionUID = -1946088618528756952L;

    public String name;

    public String clazz;

    public boolean lazy = false;

    public Bean(String name, String clazz) {
        Preconditions.checkArgument(!(StringUtils.isAllEmpty(name, clazz)));
        this.name = name;
        this.clazz = clazz;
    }
}
