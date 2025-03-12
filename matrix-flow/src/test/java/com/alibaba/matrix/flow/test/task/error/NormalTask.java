package com.alibaba.matrix.flow.test.task.error;

import com.alibaba.matrix.flow.Task;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2023/9/29 17:50.
 */
public class NormalTask implements Task<Object, Object> {

    @Override
    public Object execute(Object o) {
        System.out.println("NormalTask execute");
        return next();
    }
}
