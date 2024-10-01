package com.alibaba.matrix.config.exception;

import com.alibaba.matrix.base.message.Message;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2018/8/18 11:41.
 */
@Getter
public class ConfigCenterException extends RuntimeException {

    private static final long serialVersionUID = 1859799771165412386L;

    private final Message message;

    public ConfigCenterException(Message message) {
        super(message.getMessage());
        this.message = message;
    }

    public ConfigCenterException(Message message, Throwable cause) {
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
