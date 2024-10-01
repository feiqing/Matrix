package com.alibaba.matrix.extension.exception;

import com.alibaba.matrix.base.message.Message;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/7/24 09:51.
 */
@Getter
public class ExtensionRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 4486582697071165757L;

    private static final Throwable[] EMPTY = new Throwable[0];

    private Message message;

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

    @Override
    public String getMessage() {
        if (this.message != null) {
            return this.message.toString();
        }
        String message = super.getMessage();
        if (StringUtils.isNotEmpty(message)) {
            return message;
        }
        return Message.defaultMessage().toString();
    }
}
