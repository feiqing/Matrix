package com.alibaba.matrix.extension.test.impl;

import com.alibaba.matrix.extension.ExtensionContext;
import com.alibaba.matrix.extension.annotation.ExtensionImpl;
import com.alibaba.matrix.extension.test.ext.DemoBaseExt;

import java.util.Arrays;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/19 16:18.
 */
@ExtensionImpl(code = "code.yhb")
public class YhbDemoBaseExtImpl implements DemoBaseExt {

    @Override
    public Object test(Object... args) {
        System.out.println("YhbDemoBaseExtImpl thread = " + Thread.currentThread().getName() + ", ctx = " + ExtensionContext.getExtCtx("ctx") + ", args: " + Arrays.toString(args));
        return "YhbDemoBaseExtImpl(" + Arrays.toString(args) + ")";
    }
}
