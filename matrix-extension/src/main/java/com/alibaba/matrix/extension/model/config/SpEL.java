package com.alibaba.matrix.extension.model.config;

import java.io.Serializable;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class SpEL implements Serializable {

    private static final long serialVersionUID = 3177409043973537881L;

    public final String protocol;

    public final String path;

    public SpEL(String protocol, String path) {
        this.protocol = protocol;
        this.path = path;
    }
}
