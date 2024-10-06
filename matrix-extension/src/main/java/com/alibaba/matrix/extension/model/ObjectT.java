package com.alibaba.matrix.extension.model;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class ObjectT {

    public String clazz;

    public String args;

    public boolean lazy = false;

    public ObjectT(String clazz) {
        this.clazz = clazz;
    }
}
