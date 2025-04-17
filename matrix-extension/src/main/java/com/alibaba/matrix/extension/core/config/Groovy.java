package com.alibaba.matrix.extension.core.config;

import java.io.Serializable;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
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
