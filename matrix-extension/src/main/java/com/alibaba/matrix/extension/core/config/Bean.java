package com.alibaba.matrix.extension.core.config;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class Bean implements Serializable {

    private static final long serialVersionUID = -1946088618528756952L;

    public String name;

    public String clazz;

    public Bean(String clazz) {
        this(null, clazz);
    }

    public Bean(String name, String clazz) {
        Preconditions.checkArgument(!(StringUtils.isAllEmpty(name, clazz)));
        this.name = name;
        this.clazz = clazz;
    }
}
