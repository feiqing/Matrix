package com.alibaba.matrix.extension.test.impl.base;

import com.alibaba.matrix.extension.test.ext.DemoScriptExt;
import org.springframework.stereotype.Component;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/8 22:05.
 */
@Component
public class BlankDemoScriptExtImpl implements DemoScriptExt {

    @Override
    public Object apply(Object arg0, Object arg1, Object arg3) {
        System.out.println("base");
        return null;
    }
}
