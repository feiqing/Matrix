package com.alibaba.matrix.job;

import lombok.Getter;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2020/9/18 13:41.
 */
@Getter
public class JobExecuteException extends RuntimeException {

    private static final long serialVersionUID = 4486582697071165757L;
    
    private final Throwable[] causes;

    public JobExecuteException(Throwable[] causes) {
        this.causes = causes;
    }

    public JobExecuteException(String message, Throwable[] causes) {
        super(message);
        this.causes = causes;
    }

    public JobExecuteException(String message, Throwable cause, Throwable[] causes) {
        super(message, cause);
        this.causes = causes;
    }

    public JobExecuteException(Throwable cause, Throwable[] causes) {
        super(cause);
        this.causes = causes;
    }

    public JobExecuteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Throwable[] causes) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.causes = causes;
    }
}
