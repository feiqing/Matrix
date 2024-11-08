package com.alibaba.matrix.extension.model.config;

import java.io.Serializable;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class Groovy implements Serializable {

    private static final long serialVersionUID = 1928144919264863364L;
    
    public final String protocol;

    public final String path;

    public Groovy(String protocol, String path) {
        this.protocol = protocol;
        this.path = path;
    }
}
