package com.alibaba.matrix.extension.model;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2024/7/25 10:39.
 */
public class Http {

    public final String url;

    public String method = "POST";

    public boolean camelToUnderline = true;

    public Http(String url) {
        this.url = url;
    }
}
