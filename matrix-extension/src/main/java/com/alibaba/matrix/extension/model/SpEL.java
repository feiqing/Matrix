package com.alibaba.matrix.extension.model;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/8 17:41.
 */
public class SpEL {

    public final String protocol;

    public final String path;

    public boolean lazy = false;

    public SpEL(String protocol, String path) {
        this.protocol = protocol;
        this.path = path;
    }
}
