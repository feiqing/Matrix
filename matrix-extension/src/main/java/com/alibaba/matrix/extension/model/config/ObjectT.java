package com.alibaba.matrix.extension.model.config;

import java.io.Serializable;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
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
