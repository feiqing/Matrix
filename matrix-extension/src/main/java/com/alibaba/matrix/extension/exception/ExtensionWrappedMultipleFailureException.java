package com.alibaba.matrix.extension.exception;

import lombok.Getter;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
@Getter
public class ExtensionWrappedMultipleFailureException extends RuntimeException {

    private static final long serialVersionUID = 4486582697071165757L;

    private final Throwable[] causes;

    public ExtensionWrappedMultipleFailureException(String message, Throwable[] causes) {
        super(message);
        this.causes = causes;
    }
}
