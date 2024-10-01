package com.alibaba.matrix.extension.test.impl.base;

import com.alibaba.matrix.extension.ExtensionContext;
import com.alibaba.matrix.extension.annotation.ExtensionBase;
import com.alibaba.matrix.extension.test.ext.DemoBaseExt;

import java.util.Arrays;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/19 16:12.
 */
@ExtensionBase
public class BaseDemoBaseExtImpl implements DemoBaseExt {

    @Override
    public Object test(Object... args) {
        System.out.println("BaseDemoBaseExtImpl thread = " + Thread.currentThread().getName() + ", ctx = " + ExtensionContext.getExtCtx("ctx") + ", args: " + Arrays.toString(args));
        return "BaseDemoBaseExtImpl(" + Arrays.toString(args) + ")";
    }
}