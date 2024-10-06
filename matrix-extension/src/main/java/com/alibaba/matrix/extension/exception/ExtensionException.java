package com.alibaba.matrix.extension.exception;

import com.alibaba.matrix.base.message.Message;
import org.apache.commons.lang3.StringUtils;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class ExtensionException extends RuntimeException {

    private static final long serialVersionUID = -2060948238287068976L;

    private Message message;

    public ExtensionException() {
    }

    public ExtensionException(String message) {
        super(message);
    }

    public ExtensionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtensionException(Throwable cause) {
        super(cause);
    }

    public ExtensionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
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
