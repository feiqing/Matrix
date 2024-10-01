package com.alibaba.matrix.extension.test.impl;

import com.alibaba.matrix.extension.annotation.ExtensionImpl;
import com.alibaba.matrix.extension.test.ext.DemoShowExt;

import java.util.Arrays;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2022/7/21 21:07.
 */
@ExtensionImpl(code = "code.a", desc = "Code A")
public class CodeADemoShowExtImpl implements DemoShowExt {

    @Override
    public String desc() {
        return "code a show demo ext implementation.";
    }

    @Override
    public Object show(Object... args) {
        return "CodeADemoShowExtImpl: " + Arrays.toString(args);
    }
}
