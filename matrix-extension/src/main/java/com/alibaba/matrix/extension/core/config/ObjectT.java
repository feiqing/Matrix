package com.alibaba.matrix.extension.core.config;

import java.io.Serializable;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class ObjectT implements Serializable {

    private static final long serialVersionUID = -2894602757414467429L;

    public String clazz;

    public String arg0;

    public ObjectT(String clazz) {
        this.clazz = clazz;
    }
}
