package com.alibaba.matrix.extension.model;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2023/7/25 10:39.
 */
public class Http {

    public final String schema;

    public final String host;

    public int port = 80;

    public final String path;

    public String method = "POST";

    public boolean lazy = false;

    public Http(String schema, String host, String path) {
        this.schema = schema;
        this.host = host;
        this.path = path;
    }
}
