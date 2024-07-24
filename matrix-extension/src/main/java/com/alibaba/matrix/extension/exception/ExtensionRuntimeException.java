package com.alibaba.matrix.extension.exception;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/7/24 09:51.
 */
public class ExtensionRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 4486582697071165757L;

    public ExtensionRuntimeException() {
    }

    public ExtensionRuntimeException(String message) {
        super(message);
    }

    public ExtensionRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtensionRuntimeException(Throwable cause) {
        super(cause);
    }

    public ExtensionRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
