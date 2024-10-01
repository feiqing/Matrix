package com.alibaba.matrix.extension.model;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/25 17:23.
 */
public class ObjectT {

    public String clazz;

    public String args;

    public boolean lazy = false;

    public ObjectT(String clazz) {
        this.clazz = clazz;
    }
}
