package com.alibaba.matrix.extension.test.impl;

import com.alibaba.matrix.extension.annotation.ExtensionImpl;
import com.alibaba.matrix.extension.test.ext.ShowDemoExt;

import static com.alibaba.matrix.extension.test.constants.ExtensionConstants.SCOPE_2;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2022/7/21 21:07.
 */
@ExtensionImpl(scope = SCOPE_2, code = "my-code-1", desc = "MyCode1ShowDemoExtImpl")
public class MyCode1ShowDemoExtImpl implements ShowDemoExt {

    @Override
    public Object showDemo() {
        return "my-code-1 show demo ext impl";
    }
}
