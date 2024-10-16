package com.alibaba.matrix.extension.test.impl;

import com.alibaba.matrix.extension.annotation.ExtensionImpl;
import com.alibaba.matrix.extension.test.impl.base.BaseDemoBaseExtImpl;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2024/10/16 20:37.
 */
@ExtensionImpl(code = "code.test", lazy = true)
public class TestDemoBaseExtImpl extends BaseDemoBaseExtImpl {
    public TestDemoBaseExtImpl() {
        throw new RuntimeException("test");
    }
}
