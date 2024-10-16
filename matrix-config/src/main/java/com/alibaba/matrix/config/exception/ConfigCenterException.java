package com.alibaba.matrix.config.exception;

import lombok.Getter;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2017/4/23 22:51.
 */
@Getter
public class ConfigCenterException extends RuntimeException {

    private static final long serialVersionUID = 1859799771165412386L;

    public ConfigCenterException() {
    }

    public ConfigCenterException(String message) {
        super(message);
    }

    public ConfigCenterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigCenterException(Throwable cause) {
        super(cause);
    }

    public ConfigCenterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
