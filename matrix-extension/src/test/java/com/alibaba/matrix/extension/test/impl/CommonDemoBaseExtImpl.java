package com.alibaba.matrix.extension.test.impl;

import com.alibaba.matrix.extension.ExtensionContext;
import com.alibaba.matrix.extension.test.impl.base.BaseDemoBaseExtImpl;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2023/9/19 16:17.
 */
@Component
public class CommonDemoBaseExtImpl extends BaseDemoBaseExtImpl {

    @Override
    public Object test(Object... args) {
        System.out.println("CommonDemoBaseExtImpl thread = " + Thread.currentThread().getName() + ", ctx = " + ExtensionContext.getExtCtx("ctx") + ", args: " + Arrays.toString(args));
        return "CommonDemoBaseExtImpl(" + Arrays.toString(args) + ")";
    }
}
