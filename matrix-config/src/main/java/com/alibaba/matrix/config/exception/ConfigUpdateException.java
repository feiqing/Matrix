package com.alibaba.matrix.config.exception;

import com.alibaba.matrix.base.message.Message;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2018/8/18 11:42.
 */
@Getter
public class ConfigUpdateException extends RuntimeException {

    private static final long serialVersionUID = 6848491599325596287L;

    private final Message message;

    public ConfigUpdateException(Message message) {
        super(message.getMessage());
        this.message = message;
    }

    public ConfigUpdateException(Message message, Throwable cause) {
        super(message.getMessage(), cause);
        this.message = message;
    }

    @Override
    public String getMessage() {
        if (this.message != null) {
            return this.message.getMessage();
        }
        String message = super.getMessage();
        if (StringUtils.isNotEmpty(message)) {
            return message;
        }
        return Message.defaultMessage().getMessage();
    }
}
