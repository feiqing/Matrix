package com.alibaba.matrix.extension.test.impl;

import com.alibaba.matrix.extension.annotation.ExtensionImpl;
import com.alibaba.matrix.extension.test.impl.base.BaseDemoShowExtImpl;

import java.util.Arrays;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2022/7/21 21:08.
 */
@ExtensionImpl(code = "code.b", desc = "Code B")
public class CodeBDemoShowExtImpl extends BaseDemoShowExtImpl {

    @Override
    public String desc() {
        return "code b show demo ext implementation.";
    }

    @Override
    public Object show(Object... args) {
        return "CodeBDemoShowExtImpl: " + Arrays.toString(args);
    }
}