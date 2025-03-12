package com.alibaba.matrix.extension.test.impl;

import com.alibaba.matrix.extension.ExtensionContext;
import com.alibaba.matrix.extension.annotation.ExtensionImpl;
import com.alibaba.matrix.extension.test.ext.DemoBaseExt;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2023/9/19 16:18.
 */
@Component
@ExtensionImpl(code = "code.yhb")
public class YhbDemoBaseExtImpl implements DemoBaseExt {

    @Override
    public Object test(Object... args) {
        System.out.println("YhbDemoBaseExtImpl thread = " + Thread.currentThread().getName() + ", ctx = " + ExtensionContext.getExtCtx("ctx") + ", args: " + Arrays.toString(args));
        return "YhbDemoBaseExtImpl(" + Arrays.toString(args) + ")";
    }
}
