package com.alibaba.matrix.extension.exception;

import lombok.Getter;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
@Getter
public class ExtensionRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 4486582697071165757L;

    private static final Throwable[] EMPTY = new Throwable[0];

    private final Throwable[] causes;

    public ExtensionRuntimeException(Throwable[] causes) {
        this.causes = causes;
    }

    public ExtensionRuntimeException(String message, Throwable[] causes) {
        super(message);
        this.causes = causes;
    }

    public ExtensionRuntimeException(String message, Throwable cause, Throwable[] causes) {
        super(message, cause);
        this.causes = causes;
    }

    public ExtensionRuntimeException(Throwable cause, Throwable[] causes) {
        super(cause);
        this.causes = causes;
    }

    public ExtensionRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Throwable[] causes) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.causes = causes;
    }

    public ExtensionRuntimeException() {
        this(EMPTY);
    }

    public ExtensionRuntimeException(String message) {
        this(message, EMPTY);
    }

    public ExtensionRuntimeException(String message, Throwable cause) {
        this(message, cause, EMPTY);
    }

    public ExtensionRuntimeException(Throwable cause) {
        this(cause, EMPTY);
    }

    public ExtensionRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        this(message, cause, enableSuppression, writableStackTrace, EMPTY);
    }
}
