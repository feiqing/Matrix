package com.alibaba.matrix.extension.test.utils;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2023/9/18 22:15.
 */
public class Utils {

    public static void sleep(long ts) {
        try {
            Thread.sleep(ts);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
