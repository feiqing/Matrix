package com.alibaba.matrix.extension.model.config;

import java.io.Serializable;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class Http implements Serializable {

    private static final long serialVersionUID = -3528350565578851720L;

    public final String schema;

    public final String host;

    public int port = 80;

    public final String path;

    public String method = "POST";

    public Http(String schema, String host, String path) {
        this.schema = schema;
        this.host = host;
        this.path = path;
    }
}
