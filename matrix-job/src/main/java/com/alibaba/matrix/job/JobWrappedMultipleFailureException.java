package com.alibaba.matrix.job;

import lombok.Getter;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2020/9/18 13:41.
 */
@Getter
public class JobWrappedMultipleFailureException extends RuntimeException {

    private static final long serialVersionUID = 4486582697071165757L;

    private final Throwable[] causes;

    public JobWrappedMultipleFailureException(String message, Throwable[] causes) {
        super(message);
        this.causes = causes;
    }
}
