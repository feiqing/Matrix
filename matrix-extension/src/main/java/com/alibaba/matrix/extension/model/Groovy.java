package com.alibaba.matrix.extension.model;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2023/7/25 22:00.
 */
public class Groovy {

    public final String protocol;

    public final String path;

    public boolean lazy = false;

    public Groovy(String protocol, String path) {
        this.protocol = protocol;
        this.path = path;
    }
}
