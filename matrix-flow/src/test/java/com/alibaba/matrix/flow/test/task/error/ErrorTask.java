package com.alibaba.matrix.flow.test.task.error;

import com.alibaba.matrix.flow.Task;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/29 17:51.
 */
public class ErrorTask implements Task<Object, Object> {

    @Override
    public Object execute(Object o) {
        return ExceptionUtils.rethrow(new IOException("feiqing-test"));
    }
}
