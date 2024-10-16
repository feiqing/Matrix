package com.alibaba.matrix.extension.model.config;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class Guice implements Serializable {

    private static final long serialVersionUID = 7766476239800747986L;

    public String clazz;

    public String name;

    public Guice(String clazz) {
        Preconditions.checkArgument(StringUtils.isNotBlank(clazz));
        this.clazz = clazz;
    }
}
